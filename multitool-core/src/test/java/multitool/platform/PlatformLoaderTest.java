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

package multitool.platform;

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
