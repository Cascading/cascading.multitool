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

package multitool.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cascading.flow.FlowConnector;
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
import multitool.factory.SumFactory;
import multitool.factory.UniqueFactory;
import org.apache.hadoop.io.compress.GzipCodec;


/***
 * Base class of all hadoop based platforms.
 */
public abstract class BaseHadoopPlatform implements Platform
  {
  @Override
  public abstract FlowConnector createFlowConnector( Properties properties );

  @Override
  public Properties getPlatformProperties()
    {
    Properties properties = new Properties();
    properties.setProperty( "mapred.output.compression.codec", GzipCodec.class.getName() );
    properties.setProperty( "mapred.child.java.opts", "-server -Xmx512m" );
    properties.setProperty( "mapred.reduce.tasks.speculative.execution", "false" );
    properties.setProperty( "mapred.map.tasks.speculative.execution", "false" );
    return properties;
    }

  @Override
  public Map<String, Factory> getFactories()
    {
    PipeFactory[] pipeFactories = new PipeFactory[]{new RejectFactory( "reject" ),
                                                    new SelectFactory( "select" ),
                                                    new CutFactory( "cut" ),
                                                    new ParserFactory( "parse" ),
                                                    new RetainFactory( "retain" ),
                                                    new DiscardFactory( "discard" ),
                                                    new ParserGenFactory( "pgen" ),
                                                    new ReplaceFactory( "replace" ),
                                                    new GroupByFactory( "group" ),
                                                    new CoGroupFactory( "join" ),
                                                    new ConcatFactory( "concat" ),
                                                    new GenFactory( "gen" ),
                                                    new CountFactory( "count" ),
                                                    new SumFactory( "sum" ),
                                                    new ExpressionFactory( "expr" ),
                                                    new SelectExpressionFactory( "sexpr" ),
                                                    new DebugFactory( "debug" ),
                                                    new FileNameFactory( "filename" ),
                                                    new UniqueFactory( "unique" )};
    Map<String, Factory> factories = new HashMap<>();
    factories.put( "source", new HadoopSourceFactory( "source" ));
    factories.put( "sink", new HadoopSinkFactory( "sink" ) );
    for( PipeFactory pf : pipeFactories )
      factories.put( pf.getAlias(), pf );

    return factories;
    }

   public abstract String getName();

  }
