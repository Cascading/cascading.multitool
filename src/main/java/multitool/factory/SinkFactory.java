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

import multitool.Main;
import multitool.Main.PLATFORM;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.hadoop.SequenceFile;
import cascading.scheme.hadoop.TextDelimited;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tap.local.FileTap;
import cascading.tuple.Fields;

/**
 * {@link Factory} implementation for creating sink taps.
 */
public class SinkFactory extends TapFactory
  {
  public SinkFactory( String alias )
    {
    super( alias );
    }

  public Tap getTap( String value, Map<String, String> params, Main.PLATFORM platform )
    {
    SinkMode mode = SinkMode.KEEP;

    if( getBoolean( params, "replace" ) )
      mode = SinkMode.REPLACE;

    String select = getString( params, "select" );
    Fields sinkFields = asFields( select );

    if( sinkFields == null )
      sinkFields = Fields.ALL;

    Scheme scheme;

    if( !containsKey( params, "seqfile" ) )
      {
      String compress = getString( params, "compress", TextLine.Compress.DEFAULT.toString() );
      boolean writeHeader = getBoolean( params, "writeheader" );
      String delim = getString( params, "delim", "\t" );
      TextLine.Compress compressEnum = TextLine.Compress.valueOf( compress.toUpperCase() );
      if( platform == PLATFORM.HADOOP )
        scheme = new TextDelimited( sinkFields, compressEnum, writeHeader, delim );
      else
        scheme = new cascading.scheme.local.TextDelimited( sinkFields, writeHeader, delim );
      }
    else
      {
      if( platform == PLATFORM.HADOOP )
        scheme = new SequenceFile( sinkFields );
      else
        throw new IllegalArgumentException( "cannot use sequence file in local mode." );
      }
    if( platform == PLATFORM.HADOOP )
      return new Hfs( scheme, value, mode );

    return new FileTap( scheme, value, mode );
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