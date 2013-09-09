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

import cascading.operation.regex.RegexReplace;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class ReplaceFactory extends PipeFactory
  {
  public ReplaceFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "apply replace the regex";
    }

  public String[] getParameters()
    {
    return new String[]{"replace", "replaceAll"};
    }

  public String[] getParametersUsage()
    {
    return new String[]{"replacement string", "true if pattern should be applied more than once"};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    String replace = getString( subParams, "replace", "" );
    boolean replaceAll = getBoolean( subParams, "replaceAll", false );


    return new Each( pipe, Fields.FIRST, new RegexReplace( Fields.UNKNOWN, value, replace, replaceAll ) );
    }
  }