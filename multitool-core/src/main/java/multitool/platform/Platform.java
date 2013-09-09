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
