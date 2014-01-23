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

import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class GroupByFactory extends PipeFactory
  {
  public GroupByFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "what fields to group/sort on, grouped fields are sorted";
    }

  public String[] getParameters()
    {
    return new String[]{"secondary", "secondary.reverse"};
    }

  public String[] getParametersUsage()
    {
    return new String[]{"fields to secondary sort on", "set true to reverse secondary sort"};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    Fields groupFields = asFields( value );
    Fields secondaryFields = asFields( getString( subParams, "secondary", null ) );
    boolean isReverse = getBoolean( subParams, "secondary.reverse", false );

    if( secondaryFields == null )
      return new GroupBy( pipe, groupFields );

    return new GroupBy( pipe, groupFields, secondaryFields, isReverse );
    }
  }