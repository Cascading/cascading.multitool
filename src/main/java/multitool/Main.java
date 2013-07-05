/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import multitool.factory.CoGroupFactory;
import multitool.factory.ConcatFactory;
import multitool.factory.CountFactory;
import multitool.factory.CutFactory;
import multitool.factory.DebugFactory;
import multitool.factory.DiscardFactory;
import multitool.factory.ExpressionFactory;
import multitool.factory.Factory;
import multitool.factory.FileNameFactory;
import multitool.factory.GenFactory;
import multitool.factory.GroupByFactory;
import multitool.factory.ParserFactory;
import multitool.factory.ParserGenFactory;
import multitool.factory.PipeFactory;
import multitool.factory.RejectFactory;
import multitool.factory.ReplaceFactory;
import multitool.factory.RetainFactory;
import multitool.factory.SelectExpressionFactory;
import multitool.factory.SelectFactory;
import multitool.factory.SinkFactory;
import multitool.factory.SourceFactory;
import multitool.factory.SumFactory;
import multitool.factory.TapFactory;
import multitool.factory.UniqueFactory;
import multitool.util.Version;

import org.apache.hadoop.io.compress.GzipCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.cascade.Cascade;
import cascading.flow.Flow;
import cascading.flow.hadoop.HadoopFlowConnector;
import cascading.flow.planner.PlannerException;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.tap.Tap;

/**
 * Main class of multitool.
 */
public class Main
  {
  private static final Logger LOG = LoggerFactory.getLogger( Main.class );

  private static final String DEFAULT_JOB_NAME = "multitool";
  
  /** Factories for cascading taps. */
  static TapFactory[] TAP_FACTORIES = new TapFactory[]{new SourceFactory( "source" ), new SinkFactory( "sink" )};

  /** Factories foor cascading pipes. */
  static PipeFactory[] PIPE_FACTORIES = new PipeFactory[]{new RejectFactory( "reject" ), new SelectFactory( "select" ),
                                                          new CutFactory( "cut" ), new ParserFactory( "parse" ),
                                                          new RetainFactory( "retain" ),
                                                          new DiscardFactory( "discard" ),
                                                          new ParserGenFactory( "pgen" ),
                                                          new ReplaceFactory( "replace" ),
                                                          new GroupByFactory( "group" ), new CoGroupFactory( "join" ),
                                                          new ConcatFactory( "concat" ),
                                                          new GenFactory( "gen" ), new CountFactory( "count" ),
                                                          new SumFactory( "sum" ), new ExpressionFactory( "expr" ),
                                                          new SelectExpressionFactory( "sexpr" ),
                                                          new DebugFactory( "debug" ),
                                                          new FileNameFactory( "filename" ),
                                                          new UniqueFactory( "unique" )};

  static Map<String, Factory> factoryMap = new HashMap<String, Factory>();
  static Map<String, Option> optionMap = new HashMap<String, Option>();

  static
    {
    optionMap.put( "-h", new Option( "-h", false, null ) );
    optionMap.put( "--help", new Option( "--help", false, null ) );
    optionMap.put( "--markdown", new Option( "--markdown", false, null ) );
    optionMap.put( "--dot", new Option( "--dot", true, null ) );
    optionMap.put( "--appname", new Option( "--appname", true, null ) );


    for( Factory factory : TAP_FACTORIES )
      {
      factoryMap.put( factory.getAlias(), factory );
      optionMap.put( factory.getAlias(), new Option( factory.getAlias(), true, factory ) );
      }

    for( Factory factory : PIPE_FACTORIES )
      {
      factoryMap.put( factory.getAlias(), factory );
      optionMap.put( factory.getAlias(), new Option( factory.getAlias(), true, factory ) );
      }
    }

  private Map<String, String> options;
  private List<String[]> params;

  public static void main( String[] args )
    {

    Map<String, String> options = new LinkedHashMap<String, String>();
    List<String[]> params = new LinkedList<String[]>();

    try
      {
      for( String arg : args )
        {
        String argName = arg;
        String argVerb = arg;
        String argData = null;

        int equals_index = arg.indexOf( "=" );

        if( equals_index != -1 )
          {
          argName = arg.substring( 0, equals_index );
          argVerb = arg.substring( 0, equals_index );
          argData = arg.substring( equals_index + 1 );
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

      new Main( options, params ).execute();
      }
    catch( IllegalArgumentException exception )
      {
      System.out.println( exception.getMessage() );
      printUsage( true, false );
      }
    }

  private static void printUsage( boolean is_error, boolean gen_markdown )
    {
    if( gen_markdown )
      {
      System.out.println( "Multitool - Command Line Reference" );
      System.out.println( "==================================" );

      System.out.println( "    multitool [param] [param] ..." );
      System.out.println( "" );
      System.out.println( "first tap must be a <code>source</code> and last tap must be a <code>sink</code>" );
      System.out.println( "" );
      System.out.println( "<table>" );
      }
    else
      {
      System.out.println( "multitool [param] [param] ..." );
      System.out.println( "" );
      System.out.println( "Usage:" );
      System.out.println( "" );
      System.out.println( "first tap must be a 'source' and last tap must be a 'sink'" );
      }

    printSubHeading( gen_markdown, "options:" );
    printTableRow( gen_markdown, "-h|--help", "show this help text" );
    printTableRow( gen_markdown, "--markdown", "generate help text as GitHub Flavored Markdown" );
    printTableRow( gen_markdown, "--appname=name", "set cascading application name" );
    printTableRow( gen_markdown, "--dot=filename", "write a plan DOT file, then exit" );
    printSubHeading( gen_markdown, "taps:" );
    printFactoryUsage( gen_markdown, TAP_FACTORIES );
    printSubHeading( gen_markdown, "operations:" );
    printFactoryUsage( gen_markdown, PIPE_FACTORIES );

    if( gen_markdown )
      System.out.println( "</table>" );

    System.out.println( "" );

    if( !gen_markdown )
      printCascadingVersion();

    printLicense();

    if( is_error )
      System.exit( 1 );
    else
      System.exit( 0 );
    }

  private static void printCascadingVersion()
    {
    try
      {
      Properties versionProperties = new Properties();

      InputStream stream = Cascade.class.getClassLoader().getResourceAsStream( "cascading/version.properties" );
      versionProperties.load( stream );

      stream = Cascade.class.getClassLoader().getResourceAsStream( "cascading/build.number.properties" );
      if( stream != null )
        versionProperties.load( stream );

      String releaseMajor = versionProperties.getProperty( "cascading.release.major" );
      String releaseMinor = versionProperties.getProperty( "cascading.release.minor", null );
      String releaseBuild = versionProperties.getProperty( "cascading.build.number", null );
      String releaseFull = null;

      if( releaseMinor == null )
        releaseFull = releaseMajor;
      else if( releaseBuild == null )
        releaseFull = String.format( "%s.%s", releaseMajor, releaseMinor );
      else
        releaseFull = String.format( "%s.%s%s", releaseMajor, releaseMinor, releaseBuild );


      System.out.println( String.format( "Using Cascading %s\n", releaseFull ) );
      }
    catch( IOException exception )
      {
      System.out.println( "Unknown Cascading Version\n" );
      }
    }

  private static void printLicense()
    {
    System.out.println( "This release is licensed under the Apache Software License 2.0.\n" );
    }

  private static void printFactoryUsage( boolean gen_markdown, Factory[] factories )
    {
    for( Factory factory : factories )
      {
      printTableRow( gen_markdown, factory.getAlias(), factory.getUsage() );

      for( String[] strings : factory.getParametersAndUsage() )
        printTableRow( gen_markdown, strings[ 0 ], strings[ 1 ] );
      }
    }

  private static void printSubHeading( boolean gen_markdown, String line )
    {
    if( gen_markdown )
      System.out.println( String.format( "<tr><th>%s</th></tr>", line ) );
    else
      System.out.println( String.format( "\n%s", line ) );
    }

  private static void printTableRow( boolean gen_markdown, String option, String description )
    {
    if( gen_markdown )
      System.out.println( String.format( "<tr><td><code>%s</code></td><td>%s</td></tr>", option, description ) );
    else
      System.out.println( String.format( "  %-25s  %s", option, description ) );
    }

  public Main( List<String[]> params )
    {
    this( new LinkedHashMap<String, String>(), params );
    }

  public Main( Map<String, String> options, List<String[]> params )
    {
    if( options != null )
      this.options = options;

    this.params = params;

    if( ( this.params.size() == 0 ) || options.containsKey( "-h" ) || options.containsKey( "--help" ) || options.containsKey( "--markdown" ) )
      printUsage( false, options.containsKey( "--markdown" ) );
    else
      validateParams();
    }

  private void validateParams()
    {
    for( String[] param : params )
      {
      String alias = param[ 0 ].replaceFirst( "^([^.]+).*$", "$1" );

      if( !factoryMap.keySet().contains( alias ) )
        throw new IllegalArgumentException( "error: invalid argument: " + param[ 0 ] );
      }

    if( !params.get( 0 )[ 0 ].equals( "source" ) )
      throw new IllegalArgumentException( "error: first command must be source: " + params.get( 0 )[ 0 ] );

    if( !params.get( params.size() - 1 )[ 0 ].startsWith( "sink" ) )
      throw new IllegalArgumentException( "error: last command must be sink: " + params.get( params.size() - 1 )[ 0 ] );
    }

  private Properties getDefaultProperties()
    {
    Properties properties = new Properties();

    AppProps.setApplicationJarClass( properties, Main.class );

    properties.setProperty( "mapred.output.compression.codec", GzipCodec.class.getName() );
    properties.setProperty( "mapred.child.java.opts", "-server -Xmx512m" );
    properties.setProperty( "mapred.reduce.tasks.speculative.execution", "false" );
    properties.setProperty( "mapred.map.tasks.speculative.execution", "false" );

    AppProps.addApplicationFramework( properties, Version.MULTITOOL + ":" + Version.getReleaseFull() );

    return properties;
    }



  @SuppressWarnings("rawtypes")
  public void execute()
    {
    String dotKey = "--dot";

    try
      {
      Flow flow = plan( getDefaultProperties() );
      Version.printBanner();
      if( options.containsKey( dotKey ) )
        {
        String dot_file = options.get( dotKey );
        flow.writeDOT( dot_file );
        System.out.println( "wrote DOT file to: " + dot_file );
        System.out.println( "exiting" );
        }
      else
        {
        flow.complete();
        }
      }
    catch( PlannerException exception )
      {
      if( options.containsKey( dotKey ) )
        {
        String dotFileName = options.get( dotKey );

        exception.writeDOT( dotFileName );
        System.out.println( "wrote DOT file to: " + dotFileName );
        }

      throw exception;
      }
    }

  @SuppressWarnings("rawtypes")
public Flow plan( Properties properties )
    {
    Map<String, Pipe> pipes = new HashMap<String, Pipe>();
    Map<String, Tap> sources = new HashMap<String, Tap>();
    Map<String, Tap> sinks = new HashMap<String, Tap>();
    Pipe currentPipe = null;

    ListIterator<String[]> iterator = params.listIterator();

    while( iterator.hasNext() )
      {
      String[] pair = iterator.next();
      String key = pair[ 0 ];
      String value = pair[ 1 ];
      LOG.info( "key: {}", key );
      Map<String, String> subParams = getSubParams( key, iterator );

      Factory factory = factoryMap.get( key );

      if( factory instanceof SourceFactory )
        {
        Tap tap = ( (TapFactory) factory ).getTap( value, subParams );
        currentPipe = ( (TapFactory) factory ).addAssembly( value, subParams, currentPipe );
        sources.put( currentPipe.getName(), tap );
        }
      else if( factory instanceof SinkFactory )
        {
        sinks.put( currentPipe.getName(), ( (TapFactory) factory ).getTap( value, subParams ) );
        currentPipe = ( (TapFactory) factory ).addAssembly( value, subParams, currentPipe );
        }
      else
        {
        currentPipe = ( (PipeFactory) factory ).addAssembly( value, subParams, pipes, currentPipe );
        }

      pipes.put( currentPipe.getName(), currentPipe );
      }

    if( sources.isEmpty() )
      throw new IllegalArgumentException( "error: must have at least one source" );

    if( sinks.isEmpty() )
      throw new IllegalArgumentException( "error: must have one sink" );

    String appnameOption = "--appname";
    if (optionMap.containsKey( appnameOption ))
      AppProps.setApplicationName( properties, options.get( appnameOption ));
    
    return new HadoopFlowConnector( properties ).connect( "multitool", sources, sinks, currentPipe );
    }

  private Map<String, String> getSubParams( String key, ListIterator<String[]> iterator )
    {
    Map<String, String> subParams = new LinkedHashMap<String, String>();

    int index = iterator.nextIndex();
    for( int i = index; i < params.size(); i++ )
      {
      String current = params.get( i )[ 0 ];
      int dotIndex = current.indexOf( '.' );

      if( dotIndex == -1 )
        break;

      if( !current.startsWith( key + "." ) )
        throw new IllegalArgumentException( "error: param out of order: " + current + ", should follow: " + key );

      subParams.put( current.substring( dotIndex + 1 ), params.get( i )[ 1 ] );
      iterator.next();
      }

    return subParams;
    }
  }
