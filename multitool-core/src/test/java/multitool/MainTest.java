/*
 * Copyright (c) 2007-2015 Concurrent, Inc. All Rights Reserved.
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
