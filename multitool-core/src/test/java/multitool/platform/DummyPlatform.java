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

import java.util.Map;
import java.util.Properties;

import cascading.flow.FlowConnector;
import multitool.factory.Factory;

/**
 * stub class used for testing.
 */
public class DummyPlatform implements Platform
  {
  @Override
  public FlowConnector createFlowConnector( Properties properties )
    {
    return null;
    }

  @Override
  public Properties getPlatformProperties()
    {
    return null;
    }

  @Override
  public Map<String, Factory> getFactories()
    {
    return null;
    }

  @Override
  public String getName()
    {
    return "test-platform";
    }
  }
