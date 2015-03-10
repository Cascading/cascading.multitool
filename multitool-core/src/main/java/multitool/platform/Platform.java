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
 * Interface that defines Platform specific methods. Platforms are loaded dynamically by multitool and are required
 * to have a non-arg default constructor.
 */
public interface Platform
  {
  /**
   * Creates a platform specific FlowConnector.
   * @param properties  The for the FlowConnector.
   * @return A FlowConnector instance.
   */
  FlowConnector createFlowConnector( Properties properties );

  /**
   * Returns the default properties for the platform.
   * @return The default properties of the platform.
   */
  Properties getPlatformProperties();

  /**
   * Returns the map of factories provided by this platform. These can be used to parse the commandline and build the
   * cascading flow.
   * @return A map containing the alias and the Factories.
   */
  Map<String, Factory> getFactories();

  /***
   * Returns the name of the platform.
   * @return The name of the platform.
   */
  String getName();

  }
