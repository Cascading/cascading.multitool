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

import cascading.pipe.Pipe;

/**
 * {@link Factory} implementation for creating sink taps.
 */
public abstract class SinkFactory extends TapFactory
  {
  public SinkFactory( String alias )
    {
    super( alias );
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Pipe pipe )
    {
    return pipe;
    }

  public String getUsage()
    {
    return "an url to output path";
    }

  public String[] getParameters()
    {
    return new String[] { "select", "replace", "compress", "writeheader", "delim", "seqfile" };
    }

  public String[] getParametersUsage()
    {
    return new String[] { "fields to sink", "set true if output should be overwritten", "compression: enable, disable, or default",
        "set true to write field names as the first line", "delimiter used to separate fields",
        "write to a sequence file instead of text; writeheader, delim, and compress are ignored" };
    }
  }