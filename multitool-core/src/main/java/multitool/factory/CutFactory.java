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

package multitool.factory;

import java.util.Map;

import cascading.operation.Identity;
import cascading.operation.regex.RegexSplitter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class CutFactory extends PipeFactory
  {
  public CutFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "split the first field, and return the given fields, or all fields. 0 for first, -1 for last";
    }

  public String[] getParameters()
    {
    return new String[]{"delim"};
    }

  public String[] getParametersUsage()
    {
    return new String[]{"regex delimiter, default: '\\t' (TAB)"};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    Fields fields = asFields( value );
    String delim = getString( subParams, "delim", "\\t" );

    // cut parses the first field and returns fields out of the results
    pipe = new Each( pipe, Fields.FIRST, new RegexSplitter( delim ) );

    if( fields != null )
      pipe = new Each( pipe, fields, new Identity() );

    return pipe;
    }
  }
