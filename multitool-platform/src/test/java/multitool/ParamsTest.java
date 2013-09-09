package multitool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cascading.PlatformTestCase;
import multitool.platform.Platform;
import multitool.platform.PlatformLoader;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ParamsTest extends PlatformTestCase
  {

  private Platform platform;

  @Before
  public void setUp()
    {
    this.platform = new PlatformLoader().loadPlatform( getPlatformName() );
    }

  @Test( expected = IllegalArgumentException.class )
  public void testBadCommand() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"fudge", "path"} );
    params.add( new String[]{"sink", "path"} );

    Main.validateParams( params, platform );
    }

  @Test( expected = IllegalArgumentException.class )
  public void testBadSource() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();
    params.add( new String[]{"fudge", "path"} );
    params.add( new String[]{"sink", "path"} );
    Main.validateParams( params, platform );
    }
  @Test( expected = IllegalArgumentException.class )
  public void testBadSink() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();
    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"fudge", "path"} );
    Main.validateParams( params, platform );
    }

  @Test( expected = IllegalArgumentException.class )
  public void testSubsWrongOrder() throws IOException
    {
    List<String[]> params = new LinkedList<String[]>();

    params.add( new String[]{"source", "path"} );
    params.add( new String[]{"sink", "path"} );
    params.add( new String[]{"source.skipheader", "true"} );
    params.add( new String[]{"sink.replace", "true"} );
    Main.validateParams( params, platform );
    }
  }