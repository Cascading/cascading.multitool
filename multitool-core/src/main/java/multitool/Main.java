/*
 * Copyright (c) 2007-2015 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package multitool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import multitool.factory.Factory;
import multitool.platform.Platform;
import multitool.platform.PlatformLoader;
import multitool.platform.PlatformNotFoundException;
import multitool.util.HelpPrinter;
import multitool.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of multitool. Does the initial commandline parsing and sets up the creation of Cascading flows.
 */
public class Main
  {
  /** environment variable containing the platform to use. */
  public static final String MULTITOOL_PLATFORM = "MULTITOOL_PLATFORM";

  /** alternative environment variable containing the platform to use. */
  public static final String CASCADING_PLATFORM = "CASCADING_PLATFORM";

  /** logger. */
  private static final Logger LOG = LoggerFactory.getLogger( Main.class );
  static Map<String, Option> optionMap = new HashMap<String, Option>();

  static
    {
    optionMap.put( "-h", new Option( "-h", false ) );
    optionMap.put( "--help", new Option( "--help", false ) );
    optionMap.put( "--markdown", new Option( "--markdown", false ) );
    optionMap.put( "--platform", new Option( "--platform", true ) );
    optionMap.put( "--dot", new Option( "--dot", true ) );
    optionMap.put( "--appname", new Option( "--appname", true ) );
    optionMap.put( "--tags", new Option( "--tags", true ) );
    optionMap.put( "--version", new Option( "--version", false ) );
    }

  /**
   * The main method of Multitool.
   *
   * @param args
   */
  public static void main( String[] args )
    {
    Map<String, String> options = new LinkedHashMap<String, String>();
    List<String[]> params = new LinkedList<String[]>();

    Platform platform = null;
    try
      {
      platform = loadPlatform( args, System.getenv() );
      }
    catch( PlatformNotFoundException exception )
      {
      if ( exception.getCause() != null )
        System.err.println( exception.getCause().getMessage() );
      else
        System.err.println( exception.getMessage() );
      System.err.println( "please provide the correct platform via the --platform switch. Valid platforms are local, hadoop, and hadoop2-mr1 " );
      System.exit( 1 );
      }

    LOG.debug( "using platform '{}' ", platform.getName() );

    for( Factory factory : platform.getFactories().values() )
      optionMap.put( factory.getAlias(), new Option( factory.getAlias(), true ) );

    try
      {
      for( String arg : args )
        {
        String argName = arg;
        String argVerb = arg;
        String argData = null;

        int equalsIndex = arg.indexOf( "=" );

        if( equalsIndex != -1 )
          {
          argName = arg.substring( 0, equalsIndex );
          argVerb = arg.substring( 0, equalsIndex );
          argData = arg.substring( equalsIndex + 1 );
          }

        int dotIndex = argName.indexOf( "." );

        if( dotIndex != -1 )
          argName = argName.substring( 0, dotIndex );

        if( arg.startsWith( "-" ) )
          {
          if( optionMap.keySet().contains( argName ) && optionMap.get( argName ).isValid( argVerb, argData ) )
            options.put( argVerb, argData );
          else
            throw new IllegalArgumentException( "error: incorrect option or usage: " + arg );
          }
        else
          {
          if( optionMap.keySet().contains( argName ) )
            params.add( new String[]{argVerb, argData} );
          else
            throw new IllegalArgumentException( "error: incorrect parameter or usage: " + arg );
          }
        }

      if ( options.containsKey( "--version" ))
        {
        Version.printBanner();
        System.exit( 0 );
        }
      else if( ( params.size() == 0 ) || options.containsKey( "-h" ) || options.containsKey( "--help" )
        || options.containsKey( "--markdown" ) )
        {
        HelpPrinter printer;
        if( options.containsKey( "--markdown" ) )
          printer = new HelpPrinter( System.out, HelpPrinter.Mode.MARKDOWN, platform );
        else
          printer = new HelpPrinter( System.out, HelpPrinter.Mode.PLAIN,platform );
        printer.printHelp( platform.getFactories().values() );
        System.exit( 1 );
        }
      else
        validateParams( params, platform );

      new Runner( options, params, platform ).execute();
      }
    catch( IllegalArgumentException exception )
      {
      System.err.println( exception.getMessage() );
      new HelpPrinter( System.out, HelpPrinter.Mode.PLAIN, platform ).printHelp( platform.getFactories().values() );
      System.exit( 1 );
      }
    }

  /**
   * Method to load the platform based on the --platform parameter, the MULTITOOL_PLATFORM environment
   * variable or a .cascading configuration file in the current working directory or the user home.
   * Since the build will only put one platform in the actual jar, the name should not be necessary, but we
   * have to be careful and load, what the user intended to use, not what we find on the classpath.
   *
   * @param args The string array containing all arguments.
   * @param env  The systems environment as a Map.
   * @return The platform to use.
   */
  static Platform loadPlatform( String[] args, Map<String, String> env ) throws PlatformNotFoundException
    {
    // this could probably made into a generic piece of code shared by all our tools.
    String platformName = null;
    for( int index = 0; index < args.length; index++ )
      {
      String argument = args[ index ];
      if( argument.startsWith( "--platform=" ) )
        {
        platformName = argument.split( "=" )[ 1 ];
        break;
        }
      else if( argument.startsWith( "--platform" ) )
        {
        platformName = args[ index + 1 ];
        break;
        }
      }
    if( platformName == null && env.get( MULTITOOL_PLATFORM ) != null )
      platformName = env.get( MULTITOOL_PLATFORM );

    if( platformName == null && env.get( CASCADING_PLATFORM ) != null )
      platformName = env.get( CASCADING_PLATFORM );

    File[] files = new File[] { new File( System.getProperty( "user.dir" ) + "/.cascading" ), new File( System.getProperty( "user.home" ) + "/.cascading" ) };

    for( File configFile : files )
      {
      if( configFile.exists() && platformName == null )
        {
        Properties properties = new Properties();
        try
          {
          properties.load( new FileInputStream( configFile ) );
          platformName = properties.getProperty( "multitool.platform.name" );
          if( platformName == null )
            platformName = properties.getProperty( "cascading.platform.name" );
          else
            break;
          }
        catch( IOException exception )
          {
          throw new PlatformNotFoundException( "problem while reading cascading config file " + configFile.getAbsolutePath(),
             exception );
          }
        }
      }
    if( platformName == null )
      throw new PlatformNotFoundException( "unable to determine the platform" );

    return new PlatformLoader().loadPlatform( platformName );
    }

  static void validateParams( List<String[]> params, Platform platform )
    {
    for( String[] param : params )
      {
      String alias = param[ 0 ].replaceFirst( "^([^.]+).*$", "$1" );
      if( !platform.getFactories().keySet().contains( alias ) )
        throw new IllegalArgumentException( "error: invalid argument: " + param[ 0 ] );
      }

    if( !params.get( 0 )[ 0 ].equals( "source" ) )
      throw new IllegalArgumentException( "error: first command must be source: " + params.get( 0 )[ 0 ] );

    if( !params.get( params.size() - 1 )[ 0 ].startsWith( "sink" ) )
      throw new IllegalArgumentException( "error: last command must be sink: " + params.get( params.size() - 1 )[ 0 ] );
    }
  }