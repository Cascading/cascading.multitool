/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
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

package multitool.factory;

import java.util.Map;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;

/**
 *
 */
public class FileNameFactory extends PipeFactory
  {
  private class FileNameFunction extends BaseOperation implements Function
    {
    private FileNameFunction( Fields fieldDeclaration )
      {
      super( fieldDeclaration );
      }

    @Override
    public void operate( FlowProcess flowProcess, FunctionCall functionCall )
      {
      String filename = (String) flowProcess.getProperty( "cascading.source.path" );
      functionCall.getOutputCollector().add( new Tuple( filename ) );
      }
    }

  public FileNameFactory( String alias )
    {
    super( alias );
    }

  public String getUsage()
    {
    return "include the filename from which the current value was found";
    }

  public String[] getParameters()
    {
    return new String[]{"append", "only"};
    }

  public String[] getParametersUsage()
    {
    return new String[]{"append the filename to the record", "only return the filename"};
    }

  public Pipe addAssembly( String value, Map<String, String> subParams, Map<String, Pipe> pipes, Pipe pipe )
    {
    
    Fields fields = Fields.ALL;

    if( value != null && value.equalsIgnoreCase( "append" ) )
      fields = Fields.ALL;
    else if( value != null && value.equalsIgnoreCase( "only" ) )
      fields = Fields.RESULTS;

    return new Each( pipe, new FileNameFunction( new Fields( "filename" ) ), fields );
    }

  }