package multitool.platform;

import java.util.Map;
import java.util.Properties;

import cascading.flow.FlowConnector;
import cascading.flow.hadoop.HadoopFlowConnector;
import com.google.common.collect.Maps;
import multitool.factory.CoGroupFactory;
import multitool.factory.ConcatFactory;
import multitool.factory.CountFactory;
import multitool.factory.CutFactory;
import multitool.factory.DebugFactory;
import multitool.factory.DiscardFactory;
import multitool.factory.ExpressionFactory;
import multitool.factory.Factory;
import multitool.factory.FileNameFactory;
import multitool.factory.GenFactory;
import multitool.factory.GroupByFactory;
import multitool.factory.ParserFactory;
import multitool.factory.ParserGenFactory;
import multitool.factory.PipeFactory;
import multitool.factory.RejectFactory;
import multitool.factory.ReplaceFactory;
import multitool.factory.RetainFactory;
import multitool.factory.SelectExpressionFactory;
import multitool.factory.SelectFactory;
import multitool.factory.SumFactory;
import multitool.factory.UniqueFactory;
import org.apache.hadoop.io.compress.GzipCodec;


/***
 * Implementation of the Platform interface for hadoop1.
 */
public class HadoopPlatform implements Platform
  {
  @Override
  public FlowConnector createFlowConnector( Properties properties )
    {
    return new HadoopFlowConnector( properties );
    }

  @Override
  public Properties getPlatformProperties()
    {
    Properties properties = new Properties();
    properties.setProperty( "mapred.output.compression.codec", GzipCodec.class.getName() );
    properties.setProperty( "mapred.child.java.opts", "-server -Xmx512m" );
    properties.setProperty( "mapred.reduce.tasks.speculative.execution", "false" );
    properties.setProperty( "mapred.map.tasks.speculative.execution", "false" );
    return properties;
    }

  @Override
  public Map<String, Factory> getFactories()
    {
    PipeFactory[] pipeFactories = new PipeFactory[]{new RejectFactory( "reject" ),
                                                    new SelectFactory( "select" ),
                                                    new CutFactory( "cut" ),
                                                    new ParserFactory( "parse" ),
                                                    new RetainFactory( "retain" ),
                                                    new DiscardFactory( "discard" ),
                                                    new ParserGenFactory( "pgen" ),
                                                    new ReplaceFactory( "replace" ),
                                                    new GroupByFactory( "group" ),
                                                    new CoGroupFactory( "join" ),
                                                    new ConcatFactory( "concat" ),
                                                    new GenFactory( "gen" ),
                                                    new CountFactory( "count" ),
                                                    new SumFactory( "sum" ),
                                                    new ExpressionFactory( "expr" ),
                                                    new SelectExpressionFactory( "sexpr" ),
                                                    new DebugFactory( "debug" ),
                                                    new FileNameFactory( "filename" ),
                                                    new UniqueFactory( "unique" )};
    Map<String, Factory> factories = Maps.newHashMap();
    factories.put( "source", new HadoopSourceFactory( "source" ));
    factories.put( "sink", new HadoopSinkFactory( "sink" ) );
    for( PipeFactory pf : pipeFactories )
      factories.put( pf.getAlias(), pf );

    return factories;
    }

   public String getName()
     {
     return "hadoop";
     }

  }
