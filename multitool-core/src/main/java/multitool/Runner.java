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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.planner.PlannerException;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.tap.Tap;
import multitool.factory.Factory;
import multitool.factory.PipeFactory;
import multitool.factory.SinkFactory;
import multitool.factory.SourceFactory;
import multitool.factory.TapFactory;
import multitool.platform.Platform;
import multitool.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runner is the the class that assembles the cascading flow and runs it.
 */
public class Runner
  {
  private static final Logger LOG = LoggerFactory.getLogger( Runner.class );

  /** The map of options. */
  private Map<String, String> options;
  /** The parameters for running the flow.*/
  private List<String[]> params;
  /** The current Platform to run on. */
  private Platform platform;


  public Runner( Map<String, String> options, List<String[]> params, Platform platform )
    {
    if( options != null )
      this.options = options;

    this.params = params;
    this.platform = platform;
    }

  private Properties getDefaultProperties()
    {
    Properties properties = platform.getPlatformProperties();
    AppProps.setApplicationJarClass( properties, Main.class );
    AppProps.addApplicationFramework( properties, Version.MULTITOOL + ":" + Version.getReleaseFull() );
    return properties;
    }

  @SuppressWarnings("rawtypes")
  public void execute()
    {
    Version.printBanner();
    String dotKey = "--dot";

    try
      {
      Flow flow = plan( getDefaultProperties() );
      if( options.containsKey( dotKey ) )
        {
        String dotFile = options.get( dotKey );
        flow.writeDOT( dotFile );
        System.out.println( "wrote DOT file to: " + dotFile );
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
      LOG.debug( "key: {}", key );
      Map<String, String> subParams = getSubParams( key, iterator );

      Factory factory;

      factory = platform.getFactories().get( key );

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
    if( options.containsKey( appnameOption ) )
      AppProps.setApplicationName( properties, options.get( appnameOption ) );

    if (options.containsKey( "--tags" ))
      AppProps.addApplicationTag( properties, options.get( "--tags" ) );

    FlowConnector connector = platform.createFlowConnector( properties );
    return connector.connect( "multitool", sources, sinks, currentPipe );
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
