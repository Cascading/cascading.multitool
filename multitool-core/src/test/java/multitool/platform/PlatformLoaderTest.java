package multitool.platform;/*
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

import cascading.CascadingException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for PlatformLoader
 */
public class PlatformLoaderTest
  {

  @Test(expected = CascadingException.class)
  public void testLoadPlatformNonExisting()
    {
    String nonExistingPlatform = "foo";
    PlatformLoader loader = new PlatformLoader();
    loader.loadPlatform( nonExistingPlatform );
    }

  @Test
  public void testLoadPlatform()
    {
    String testPlatformName = "test-platform";
    PlatformLoader loader = new PlatformLoader();
    Platform platform = loader.loadPlatform( testPlatformName );
    assertNotNull( platform );
    assertTrue( platform instanceof DummyPlatform );
    }

  }
