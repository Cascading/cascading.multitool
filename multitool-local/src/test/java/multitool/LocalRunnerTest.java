package multitool;/*
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

import java.io.IOException;

import cascading.flow.Flow;
import cascading.scheme.local.TextLine;
import cascading.tap.local.FileTap;
import cascading.tuple.TupleEntryIterator;

/**
 * Tests on the 'local' platform.
 */
public class LocalRunnerTest extends RunnerTest
  {
  public TupleEntryIterator openTupleEntryIterator( Flow flow, String identifier ) throws IOException
    {
    return flow.openTapForRead( new FileTap( new TextLine(), identifier ) );
    }
  }
