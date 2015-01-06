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

import cascading.operation.expression.ExpressionFunction;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class ExpressionFactory extends PipeFactory
  {
  public ExpressionFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "use java expression as function, e.g. $0.toLowerCase()";
    }

  public String[] getParameters()
    {
    return new String[]{"args"};
    }

  public String[] getParametersUsage()
    {
    return new String[]{"the fields to use as arguments"};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    Fields fields = asFields( getString( subParams, "args", null ) );

    if( fields == null )
      fields = Fields.FIRST;

    return new Each( pipe, fields, new ExpressionFunction( Fields.size( 1 ), value, String.class ) );
    }
  }