/*
 * Copyright (c) 2007-2014 Concurrent, Inc. All Rights Reserved.
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

import cascading.operation.aggregator.Count;
import cascading.pipe.Every;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class CountFactory extends PipeFactory
  {
  public CountFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "count the number of values in the grouping";
    }

  public String[] getParameters()
    {
    return new String[]{};
    }

  public String[] getParametersUsage()
    {
    return new String[]{};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    Fields fields = asFields( value );

    if( fields == null )
      fields = Fields.ALL;

    return new Every( pipe, fields, new Count() );
    }
  }