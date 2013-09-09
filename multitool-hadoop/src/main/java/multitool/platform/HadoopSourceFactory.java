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
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import multitool.factory.SourceFactory;

/**
 * Factory for data hadoop sources.
 */
public class HadoopSourceFactory extends SourceFactory
  {
  public HadoopSourceFactory( String alias )
    {
    super( alias );
    }

  public Tap getTap( String value, Map<String, String> params )
    {
    String numFields = getString( params, "seqfile", "" );

    if( containsKey( params, "delim" ) )
      {
      String delimiter = getString( params, "delim", "\t" );
      boolean hasHeader = getBoolean( params, "hasheader" );
      Scheme scheme = new TextDelimited( Fields.ALL, null, hasHeader, hasHeader, delimiter );
      return new Hfs( scheme, value );
      }
    else if( containsKey( params, "seqfile" ) || numFields.equalsIgnoreCase( "true" ) )
      return new Hfs( new SequenceFile( Fields.ALL ), value );

    else if( numFields == null || numFields.isEmpty() )
      {
      return new Hfs( new TextLine( Fields.size( 2 ) ), value );
      }
    else
      {
      int size = Integer.parseInt( numFields );
      return new Hfs( new SequenceFile( Fields.size( size ) ), value );
      }
    }
  }