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

package multitool.platform;

import java.util.Map;

import cascading.scheme.Scheme;
import cascading.scheme.hadoop.SequenceFile;
import cascading.scheme.hadoop.TextDelimited;
import cascading.scheme.hadoop.TextLine;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import multitool.factory.SinkFactory;

/**
 * {@link multitool.factory.Factory} implementation for creating sink taps.
 */
public class HadoopSinkFactory extends SinkFactory
  {
  public HadoopSinkFactory( String alias )
    {
    super( alias );
    }

  public Tap getTap( String value, Map<String, String> params )
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
      String delimiter = getString( params, "delim", "\t" );
      TextLine.Compress compressEnum = TextLine.Compress.valueOf( compress.toUpperCase() );
      scheme = new TextDelimited( sinkFields, compressEnum, writeHeader, delimiter );
      }
    else
      {
      scheme = new SequenceFile( sinkFields );
      }
    return new Hfs( scheme, value, mode );
    }
  }