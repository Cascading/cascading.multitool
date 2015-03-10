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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import cascading.PlatformTestCase;
import cascading.flow.Flow;
import cascading.tuple.TupleEntryIterator;
import multitool.platform.Platform;
import multitool.platform.PlatformLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class RunnerTest extends PlatformTestCase
  {
  private static final long serialVersionUID = -8498283088222385072L;

  public static final String trackData = "../src/main/resources/data/track.100.txt";
  public static final String topicData = "../src/main/resources/data/topic.100.txt";
  public static final String artistData = "../src/main/resources/data/artist.100.txt";
  public static final String songsData = "../src/main/resources/data/songs.100.txt";

  public static final String outputPath = "build/test/output";

  List<String[]> params;
  LinkedHashMap<String, String> options;
  Platform platform;

  @After
  public void tearDown() throws IOException
    {
    Path path = Paths.get( outputPath );
    if( Files.exists( path ) )
      Files.walkFileTree( path, new SimpleFileVisitor<Path>()
      {
      @Override
      public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException
        {
        Files.delete( file );
        return FileVisitResult.CONTINUE;
        }

      @Override
      public FileVisitResult postVisitDirectory( Path dir, IOException exc ) throws IOException
        {
        Files.delete( dir );
        return FileVisitResult.CONTINUE;
        }

      } );

    }

  @Before
  public void setUp()
    {
    params = new LinkedList<String[]>();
    options = new LinkedHashMap<String, String>();
    platform = new PlatformLoader().loadPlatform( getPlatformName() );
    }

  @Test
  public void testCopy() throws IOException
    {

    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );
    params.add( new String[]{"sink", outputPath + "/simple"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 99, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){11}$" ) );
    iterator.close();
    }

  @Test
  public void testDelimited() throws IOException
    {

    params.add( new String[]{"source", songsData} );
    params.add( new String[]{"source.delim", null} );
    params.add( new String[]{"source.hasheader", "true"} );

    params.add( new String[]{"sink", outputPath + "/delim"} );
    params.add( new String[]{"sink.replace", "true"} );
    params.add( new String[]{"sink.writeheader", "true"} );
    params.add( new String[]{"sink.select", "name,album"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 32, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){2}$" ) ); // line
    iterator.close();
    }

  @Test
  public void testCut() throws IOException
    {

    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "1,2"} );

    params.add( new String[]{"sink", outputPath + "/cut"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 99, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){2}$" ) );
    iterator.close();
    }

  @Test
  public void testDiscard() throws IOException
    {

    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "1,2"} );
    params.add( new String[]{"discard", "1"} );

    params.add( new String[]{"sink", outputPath + "/discard"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 99, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){1}$" ) );
    iterator.close();
    }

  @Test
  public void testSelectReject() throws IOException
    {

    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"select", "w"} );
    params.add( new String[]{"reject", "o"} );

    params.add( new String[]{"sink", outputPath + "/selectreject"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 2, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){11}$" ) );

    iterator.close();
    }

  @Test
  public void testSelectFilename() throws IOException
    {
    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"select", "w"} );
    params.add( new String[]{"filename", "append"} );
    params.add( new String[]{"group", "0"} );
    params.add( new String[]{"unique", ""} );

    params.add( new String[]{"sink", outputPath + "/selectfilename"} );
    params.add( new String[]{"sink.replace", "true"} );
    params.add( new String[]{"sink.parts", "0"} );

    // this only works in hadoop mode
    if( getPlatformName().equals( "local" ) || getPlatformName().equals( "hadoop2-tez" ) )
       return;
    else
      {
      Flow flow = createFlow();
      flow.complete();

      String identifier = flow.getSink().getIdentifier().toString();
      TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
      validateLength( iterator, 16, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){12}file:.*/data/track.100.txt$" ) );

      iterator.close();
      }
    }

  @Test
  public void testSort() throws IOException
    {

    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", null} );
    params.add( new String[]{"group", "0"} );

    params.add( new String[]{"sink", outputPath + "/sort"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 99, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){11}$" ) );
    iterator.close();
    }

  @Test
  public void testConcat() throws IOException
    {
    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "2,3"} );
    params.add( new String[]{"concat", null} );
    params.add( new String[]{"concat.delim", "|"} );

    params.add( new String[]{"sink", outputPath + "/concat"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();

    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 99, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*)$" ) );
    iterator.close();
    }

  @Test
  public void testWordCount() throws IOException
    {
    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"expr", "$0.toLowerCase()"} );
    params.add( new String[]{"gen", "(?<!\\pL)(?=\\pL)[^\\s]*(?<=\\pL)(?!\\pL)"} );
    params.add( new String[]{"group", "0"} );
    params.add( new String[]{"count", null} );
    params.add( new String[]{"group", "1"} );

    params.add( new String[]{"sink", outputPath + "/wordcount"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();

    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 395, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){2}$" ) );
    iterator.close();
    }

  @Test
  public void testParseValues() throws IOException
    {
    params.add( new String[]{"source", topicData} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "0"} );
    params.add( new String[]{"pgen", "(\\b[12][09][0-9]{2}\\b)"} );
    params.add( new String[]{"group", "0"} );
    params.add( new String[]{"count", "0"} ); // adds count field
    params.add( new String[]{"group", "1"} );

    params.add( new String[]{"sink", outputPath + "/parsevalues"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 4, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){2}$" ) );
    iterator.close();
    }

  @Test
  public void testJoin() throws IOException
    {
    params.add( new String[]{"source", trackData} );
    params.add( new String[]{"source.name", "lhs"} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "3"} );
    params.add( new String[]{"gen", ""} );
    params.add( new String[]{"gen.delim", " "} );

    params.add( new String[]{"debug", ""} );
    params.add( new String[]{"debug.prefix", "lhs"} );

    params.add( new String[]{"source", artistData} );
    params.add( new String[]{"source.name", "rhs"} );
    params.add( new String[]{"source.skipheader", "true"} );

    params.add( new String[]{"cut", "0"} );
    params.add( new String[]{"gen", ""} );
    params.add( new String[]{"gen.delim", " "} );

    params.add( new String[]{"debug", ""} );
    params.add( new String[]{"debug.prefix", "rhs"} );

    params.add( new String[]{"join", ""} );
    params.add( new String[]{"join.lhs", "lhs"} );
    params.add( new String[]{"join.rhs", "rhs"} );

    params.add( new String[]{"count", ""} );

    params.add( new String[]{"sink", outputPath + "/join"} );
    params.add( new String[]{"sink.replace", "true"} );

    Flow flow = createFlow();
    flow.complete();

    String identifier = flow.getSink().getIdentifier().toString();
    TupleEntryIterator iterator = openTupleEntryIterator( flow, identifier );
    validateLength( iterator, 5, 2, Pattern.compile( "^[0-9]+(\\t[^\\t]*){3}$" ) );
    iterator.close();
    }


  @Test
  public void testGetName()
    {
    assertEquals( getPlatformName(), platform.getName() );
    }

  private Flow createFlow()
    {
    return new Runner( options, params, platform ).plan( platform.getPlatformProperties() );
    }

  public abstract TupleEntryIterator openTupleEntryIterator( Flow flow, String identifier ) throws IOException;

  }
