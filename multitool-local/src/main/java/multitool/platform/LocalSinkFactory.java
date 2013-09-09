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
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.local.FileTap;
import cascading.tuple.Fields;
import multitool.factory.SinkFactory;

/**
 * {@link multitool.factory.Factory} implementation for creating sink taps.
 */
public class LocalSinkFactory extends SinkFactory
  {
  public LocalSinkFactory( String alias )
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

    if( containsKey( params, "seqfile" ) )
      {
      throw new IllegalArgumentException( "cannot use sequence file in local mode." );
      }
    else
      {
      boolean writeHeader = getBoolean( params, "writeheader" );
      String delimiter = getString( params, "delim", "\t" );
      scheme = new cascading.scheme.local.TextDelimited( sinkFields, writeHeader, delimiter );
      }
    return new FileTap( scheme, value, mode );
    }
  }