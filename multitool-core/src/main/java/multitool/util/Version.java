/*
 * Copyright (c) 2007-2014 Concurrent, Inc. All Rights Reserved.
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

package multitool.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Version
  {
  private static final Logger LOG = LoggerFactory.getLogger( Version.class );

  private static boolean printedVersion = false;

  public static final String MULTITOOL_BUILD_NUMBER = "multitool.build.number";
  public static final String MULTITOOL_RELEASE_DATE = "multitool.release.releasedate";
  public static final String MULTITOOL_RELEASE_COMMIT = "multitool.release.commit";

  public static final String MULTITOOL = "Multitool";

  public static Properties versionProperties;

  private static synchronized Properties getVersionProperties()
    {
    try
      {
      if( versionProperties == null )
        {
        versionProperties = loadVersionProperties();

        if( versionProperties.isEmpty() )
          LOG.warn( "unable to load version information" );
        }
      }
    catch( IOException exception )
      {
      LOG.warn( "unable to load version information", exception );
      versionProperties = new Properties();
      }

    return versionProperties;
    }

  public static synchronized void printBanner()
    {
    // only print once
    if( printedVersion )
      return;

    printedVersion = true;

    String version = getVersionString();

    if( version != null )
      LOG.info( version );
    }

  public static String getVersionString()
    {
    if( getVersionProperties().isEmpty() )
      return null;

    String releaseVersion;

    if( getReleaseBuild() == null || getReleaseBuild().isEmpty() )
      releaseVersion = String.format( "Concurrent, Inc - %s %s", MULTITOOL, getReleaseFull() );
    else
      releaseVersion = String.format( "Concurrent, Inc - %s %s-%s", MULTITOOL, getReleaseFull(), getReleaseBuild() );

    return releaseVersion;
    }

  public static String getRelease()
    {
    if( getVersionProperties().isEmpty() )
      return null;

    if( getReleaseBuild() == null || getReleaseBuild().isEmpty() )
      return String.format( "%s", getReleaseFull() );
    else
      return String.format( "%s-%s", getReleaseFull(), getReleaseBuild() );
    }

  public static String getReleaseFull()
    {
    return String.format( "%s-%s", getReleaseDate(), getReleaseCommit() );
    }

  public static String getReleaseBuild()
    {
    return getVersionProperties().getProperty( MULTITOOL_BUILD_NUMBER );
    }

  public static String getReleaseCommit()
    {
    return getVersionProperties().getProperty( MULTITOOL_RELEASE_COMMIT );
    }

  public static String getReleaseDate()
    {
    return getVersionProperties().getProperty( MULTITOOL_RELEASE_DATE );
    }

  public static Properties loadVersionProperties() throws IOException
    {
    Properties properties = new Properties();
    InputStream stream = Version.class.getClassLoader().getResourceAsStream( "multitool/version.properties" );
    if( stream == null )
      return properties;
    try
      {
      properties.load( stream );
      }
    finally
      {
      stream.close();
      }
    stream = Version.class.getClassLoader().getResourceAsStream( "multitool/build.number.properties" );
    if( stream != null )
      {
      try
        {
        properties.load( stream );
        }
      finally
        {
        stream.close();
        }
      }
    return properties;
    }

  }
