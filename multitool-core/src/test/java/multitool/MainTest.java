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

package multitool;

import java.util.HashMap;
import java.util.Map;

import cascading.CascadingException;
import multitool.platform.DummyPlatform;
import multitool.platform.Platform;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Main.
 */
public class MainTest
  {
  @Test( expected = CascadingException.class )
  public void testLoadPlatformEmtpyArgumentsNoEnvironmentVariable()
    {
    Main.loadPlatform( new String[]{}, new HashMap<String, String>() );
    }

  @Test
  public void testLoadPlatformFromArguments()
    {
    Platform platform = Main.loadPlatform( new String[]{"--platform=test-platform"}, System.getenv() );
    assertNotNull( platform );
    assertTrue(platform instanceof DummyPlatform);
    assertEquals( "test-platform", platform.getName() );
    }

  @Test
  public void testLoadPlatformFromArguments2()
    {
    Platform platform = Main.loadPlatform( new String[]{"--platform", "test-platform"}, System.getenv());
    assertNotNull( platform );
    assertTrue( platform instanceof DummyPlatform );
    assertEquals( "test-platform", platform.getName() );
    }

  @Test
  public void testLoadPlatformFromEnvironmentVariable()
    {
    Map<String, String> newEnv = new HashMap<String, String>();
    newEnv.put( Main.MULTITOOL_PLATFORM, "test-platform" );
    Platform platform = Main.loadPlatform( new String[]{}, newEnv );
    assertNotNull( platform );
    assertTrue( platform instanceof DummyPlatform );
    assertEquals( "test-platform", platform.getName() );
    }

  @Test
  public void testLoadPlatformFromAlternativeEnvironmentVariable()
    {
    Map<String, String> newEnv = new HashMap<String, String>();
    newEnv.put( Main.CASCADING_PLATFORM, "test-platform" );
    Platform platform = Main.loadPlatform( new String[]{}, newEnv );
    assertNotNull( platform );
    assertTrue( platform instanceof DummyPlatform );
    assertEquals( "test-platform", platform.getName() );
    }


  @Test
  public void testLoadPlatformArgumentTakesPrecedence()
    {
    Map<String, String> newEnv = new HashMap<String, String>();
    newEnv.put( Main.MULTITOOL_PLATFORM, "does-not-work" );
    Platform platform = Main.loadPlatform( new String[]{"--platform=test-platform"}, newEnv );
    assertNotNull( platform );
    assertTrue( platform instanceof DummyPlatform );
    assertEquals( "test-platform", platform.getName() );
    }
  }
