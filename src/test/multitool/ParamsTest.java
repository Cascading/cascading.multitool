/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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

package multitool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import cascading.CascadingTestCase;

/**
 *
 */
public class ParamsTest extends CascadingTestCase
  {
  public ParamsTest()
    {
    super( "params tests" );
    }

  public void testBadCommand() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"fudge", "path"} );
    params.add( new String[]{"sink", "path"} );

    try
      {
      new Main( params ).plan( new Properties() );
      fail( "did not catch out of order params" );
      }
    catch( IllegalArgumentException exception )
      {
      // ignore
      }
    }

  public void testBadSource() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"fudge", "path"} );
    params.add( new String[]{"sink", "path"} );

    try
      {
      new Main( params ).plan( new Properties() );
      fail( "did not catch out of order params" );
      }
    catch( IllegalArgumentException exception )
      {
      // ignore
      }
    }

  public void testBadSink() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"fudge", "path"} );

    try
      {
      new Main( params ).plan( new Properties() );
      fail( "did not catch out of order params" );
      }
    catch( IllegalArgumentException exception )
      {
      // ignore
      }
    }

  public void testSubsWrongOrder() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"sink", "path"} );
    params.add( new String[]{"source.skipheader", "true"} );
    params.add( new String[]{"sink.replace", "true"} );

    try
      {
      new Main( params ).plan( new Properties() );
      fail( "did not catch out of order params" );
      }
    catch( IllegalArgumentException exception )
      {
      // ignore
      }
    }
  }