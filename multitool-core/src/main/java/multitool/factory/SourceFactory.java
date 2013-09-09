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

package multitool.factory;

import java.util.Map;

import cascading.operation.Identity;
import cascading.operation.expression.ExpressionFilter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 * Factory for data sources.
 */
public abstract class SourceFactory extends TapFactory
  {
  public SourceFactory( String alias )
    {
    super( alias );
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Pipe pipe )
    {
    String name = getString( subParams, "name" );

    if( name == null || name.isEmpty() )
      name = "multitool";

    pipe = new Pipe( name );

    if( getBoolean( subParams, "skipheader" ) )
      pipe = new Each( pipe, new Fields( 0 ), new ExpressionFilter( "$0 == 0", Long.class ) );

    String sequence = getString( subParams, "seqfile" );

    if( getBoolean( subParams, "hasheader" ) )
      pipe = new Each( pipe, Fields.ALL, new Identity() );
    else if( sequence == null || sequence.isEmpty() )
      pipe = new Each( pipe, new Fields( 1 ), new Identity() );

    return pipe;
    }

  public String getUsage()
    {
    return "an url to input data";
    }

  public String[] getParameters()
    {
    return new String[]{ "name", "skipheader", "hasheader", "delim", "seqfile" };
    }

  public String[] getParametersUsage()
    {
    return new String[]{ "name of this source, required if more than one", "set true if the first line should be skipped",
        "set true if the first line should be used for field names", "delimiter used to separate fields",
        "read from a sequence file instead of text; specify N fields, or 'true'" };
    }
  }
