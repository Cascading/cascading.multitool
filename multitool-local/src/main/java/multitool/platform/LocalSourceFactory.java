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

package multitool.platform;

import java.util.Map;

import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tap.local.FileTap;
import cascading.tuple.Fields;
import multitool.factory.SourceFactory;

/**
 * Factory for data sources.
 */
public class LocalSourceFactory extends SourceFactory
  {
  public LocalSourceFactory( String alias )
    {
    super( alias );
    }

  public Tap getTap( String value, Map<String, String> params )
    {
    String numFields = getString( params, "seqfile", "" );

    if( containsKey( params, "delim" ) )
      {
      String delim = getString( params, "delim", "\t" );
      boolean hasHeader = getBoolean( params, "hasheader" );

      Scheme scheme = new cascading.scheme.local.TextDelimited( Fields.ALL, hasHeader, hasHeader, delim );
      return new FileTap( scheme, value );
      }
    else if( containsKey( params, "seqfile" ) || numFields.equalsIgnoreCase( "true" ) )
      throw new IllegalArgumentException( "cannot use sequence files in local mode" );

    else if( numFields == null || numFields.isEmpty() )
      {
      return new FileTap( new cascading.scheme.local.TextLine( Fields.size( 2 ) ), value );
      }
    else
      {
      int size = Integer.parseInt( numFields );
      return new FileTap( new cascading.scheme.local.TextLine( Fields.size( size ) ), value );
      }
    }
  }
