
package multitool.platform;

import java.util.Map;
import java.util.Properties;

import cascading.flow.FlowConnector;
import cascading.flow.tez.Hadoop2TezFlowConnector;
import multitool.factory.Factory;
import org.apache.hadoop.io.compress.GzipCodec;

/**
 * Implementation of the Platform interface for hadoop2-mr1.
 */
public class Hadoop2TezPlatform extends BaseHadoopPlatform
  {

  @Override
  public Properties getPlatformProperties()
    {
    Properties properties = new Properties();
    properties.setProperty( "mapred.output.compression.codec", GzipCodec.class.getName() );
    properties.setProperty( "cascading.flow.runtime.gather.partitions.num", "4" );
    properties.setProperty( "tez.am.tez-ui.webservice.enable", "false" );
    return properties;
    }

  @Override
  public Map<String, Factory> getFactories()
    {
    Map<String, Factory> factoryMap =  super.getFactories();
    factoryMap.remove( "filename" );
    return factoryMap;
    }

  @Override
  public FlowConnector createFlowConnector( Properties properties )
    {
    return new Hadoop2TezFlowConnector( properties );
    }

  @Override
  public String getName()
    {
    return "hadoop2-tez";
    }


  }
