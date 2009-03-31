/*
 * Copyright (c) 2007-2009 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package multitool.facctory;

import java.util.Map;

import cascading.operation.regex.RegexFilter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

/**
 *
 */
public class SelectFactory extends PipeFactory
  {
  public SelectFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "regex, matches are kept";
    }

  public String[] getParameters()
    {
    return new String[0];
    }

  public String[] getParametersUsage()
    {
    return new String[0];
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Pipe pipe )
    {
    return new Each( pipe, Fields.ALL, new RegexFilter( value, false ) );
    }
  }
