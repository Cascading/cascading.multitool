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

/**
 * Custom exception used to signal, that no Cascading platform can be found or something went wrong, while loading it.
 */
public class PlatformNotFoundException extends CascadingException
  {
  /**
   * Constructor PlatformNotFoundException creates a new PlatformNotFoundException instance.
   *
   * @param string of type String
   */
  public PlatformNotFoundException( String string )
    {
    super( string );
    }

  /**
   * Constructor PlatformNotFoundException creates a new PlatformNotFoundException instance.
   *
   * @param string    of type String
   * @param throwable of type Throwable
   */
  public PlatformNotFoundException( String string, Throwable throwable )
    {
    super( string, throwable );
    }

  /**
   * Constructor PlatformNotFoundException creates a new PlatformNotFoundException instance.
   *
   * @param throwable of type Throwable
   */
  public PlatformNotFoundException( Throwable throwable )
    {
    super( throwable );
    }


  }
