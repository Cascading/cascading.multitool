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

package multitool.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import multitool.factory.Factory;
import multitool.factory.TapFactory;
import multitool.platform.Platform;

/**
 *  Class that encapsulates the printing of the help/markdown creation. It has two different modes PLAIN and MARKDOWN.
 *  PLAIN is for printing to a terminal, MARKDOWN is for creating github flavored markdow.
 */
public class HelpPrinter
  {
  private final PrintStream printStream;
  private final Mode mode;
  private final Platform platform;

  /**
   * The help printer has two modes of operation, which are represented by an enum.
   */
  public enum Mode {MARKDOWN, PLAIN}

  /**
   * Constructs a new HelpPrinter.
   * @param printStream The stream to write the help to.
   * @param mode  The mode of operation.
   * @param platform
   */
  public HelpPrinter( PrintStream printStream, Mode mode, Platform platform )
    {
    this.printStream = printStream;
    this.mode = mode;
    this.platform = platform;
    }


  /**
   * Prints the help for the given set of Factories.
   * @param factories
   */
  public void printHelp( Collection<Factory> factories )
    {
    if( mode == Mode.MARKDOWN )
      {
      printStream.println( "Multitool - Command Line Reference" );
      printStream.println( "==================================" );

      printStream.println( "    multitool [param] [param] ..." );
      printStream.println( "" );
      printStream.println( "first tap must be a <code>source</code> and last tap must be a <code>sink</code>" );
      printStream.println( "" );
      printStream.println( "<table>" );
      }
    else
      {
      printStream.println( "multitool [param] [param] ..." );
      printStream.println( "" );
      printStream.println( "Usage:" );
      printStream.println( "" );
      printStream.println( "first tap must be a 'source' and last tap must be a 'sink'" );
      }

    printSubHeading( "options:" );
    printTableRow( "-h|--help", "show this help text" );
    printTableRow( "--markdown", "generate help text as GitHub Flavored Markdown" );
    printTableRow( "--appname=name", "sets cascading application name" );
    printTableRow( "--tags=comma,separated", "sets cascading application tags, comma separated" );
    printTableRow( "--platform=name", "name of the cascading platform to use" );
    printTableRow( "--version", "prints the version and exits" );
    printTableRow( "--dot=filename", "write a plan DOT file, then exit" );


    List<Factory> tapFactories = new ArrayList<>();
    List<Factory> operationFactories = new ArrayList<>();

    for( Factory factory : factories )
      {
      if( factory instanceof TapFactory )
        tapFactories.add( factory );
      else
        operationFactories.add( factory );
      }
    printSubHeading( "taps:" );
    printFactoryUsage( tapFactories );
    printSubHeading( "operations:" );
    printFactoryUsage( operationFactories );

    if( mode == Mode.MARKDOWN )
      printStream.println( "</table>" );

    printStream.println( "" );

    if( mode != Mode.MARKDOWN )
      printStream.println( String.format( "Using Cascading %s (platform=%s) \n", cascading.util.Version.getReleaseFull(), platform.getName() ) );

    printStream.println( "This release is licensed under the Apache Software License 2.0.\n" );
    }

  private void printFactoryUsage( List<Factory> factories )
    {
    for( Factory factory : factories )
      {
      printTableRow( factory.getAlias(), factory.getUsage() );

      for( String[] strings : factory.getParametersAndUsage() )
        printTableRow( strings[ 0 ], strings[ 1 ] );
      }
    }

  private void printSubHeading( String line )
    {
    if( mode == Mode.MARKDOWN )
      printStream.println( String.format( "<tr><th>%s</th></tr>", line ) );
    else
      printStream.println( String.format( "\n%s", line ) );
    }

  private void printTableRow( String option, String description )
    {
    if( mode == Mode.MARKDOWN )
      printStream.println( String.format( "<tr><td><code>%s</code></td><td>%s</td></tr>", option, description ) );
    else
      printStream.println( String.format( "  %-25s  %s", option, description ) );
    }

  }
