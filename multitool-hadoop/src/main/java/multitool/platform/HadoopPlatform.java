/*
 * Copyright (c) 2007-2015 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 */

package multitool.platform;

import java.util.Properties;

import cascading.flow.FlowConnector;
import cascading.flow.hadoop.HadoopFlowConnector;

/**
 *
 */
public class HadoopPlatform extends BaseHadoopPlatform
  {
  @Override
  public FlowConnector createFlowConnector( Properties properties )
    {
    return new HadoopFlowConnector( properties );
    }

  @Override
  public String getName()
    {
    return "hadoop";
    }
  }
