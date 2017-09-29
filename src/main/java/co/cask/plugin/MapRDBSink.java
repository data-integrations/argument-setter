/*
 * Copyright © 2017 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.plugin;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.batch.Output;
import co.cask.cdap.api.data.batch.OutputFormatProvider;
import co.cask.cdap.api.data.format.StructuredRecord;
import co.cask.cdap.api.dataset.lib.KeyValue;
import co.cask.cdap.etl.api.Emitter;
import co.cask.cdap.etl.api.batch.BatchSink;
import co.cask.cdap.etl.api.batch.BatchSinkContext;
import co.cask.cdap.format.StructuredRecordStringConverter;
import co.cask.hydrator.common.ReferenceBatchSink;
import co.cask.hydrator.common.ReferencePluginConfig;
import co.cask.hydrator.common.batch.JobUtils;
import com.mapr.db.MapRDB;
import com.mapr.db.mapreduce.DocumentSerialization;
import com.mapr.db.mapreduce.TableOutputFormat;
import com.mapr.db.mapreduce.ValueSerialization;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.StringUtils;
import org.ojai.Document;
import org.ojai.Value;
import org.ojai.json.impl.JsonValueBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Sink for writing to the MapR-db table.
 */

@Plugin(type = BatchSink.PLUGIN_TYPE)
@Name("MapRDB")
@Description("MapR-DB Sink")
public class MapRDBSink extends ReferenceBatchSink<StructuredRecord, Value, Document> {

  private MapRDBSinkConfig config;

  public MapRDBSink(MapRDBSinkConfig config) {
    super(config);
    this.config = config;
  }

  @Override
  public void prepareRun(BatchSinkContext context) throws Exception {
    Job job;
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    try {
      job = JobUtils.createInstance();
    } finally {
      // Switch back to the original
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }

    Configuration conf = job.getConfiguration();
    context.addOutput(Output.of(config.referenceName, new MapRDBOutputFormatProvider(config, conf)));
  }

  private class MapRDBOutputFormatProvider implements OutputFormatProvider {

    private final Map<String, String> conf;

    MapRDBOutputFormatProvider(MapRDBSinkConfig mapRDBSinkConfig, Configuration hConf) {
      this.conf = new HashMap<>();
      conf.put(TableOutputFormat.OUTPUT_TABLE, mapRDBSinkConfig.tableName);

      String [] serializationClasses = {
        hConf.get("io.serializations"),
        ValueSerialization.class.getName(),
        DocumentSerialization.class.getName()
      };
      conf.put("io.serializations", StringUtils.arrayToString(serializationClasses));
    }

    @Override
    public String getOutputFormatClassName() {
      return TableOutputFormat.class.getName();
    }

    @Override
    public Map<String, String> getOutputFormatConfiguration() {
      return conf;
    }
  }

  @Override
  public void transform(StructuredRecord input, Emitter<KeyValue<Value, Document>> emitter) throws Exception {
    Document document = MapRDB.newDocument();
    document.setId((String) input.get(config.key));
    document.set("body", StructuredRecordStringConverter.toJsonString(input));
    emitter.emit(new KeyValue<Value, Document>(JsonValueBuilder.initFrom(config.key), document));
  }

  /**
   *
   */
  public static class MapRDBSinkConfig extends ReferencePluginConfig {

    @Description("Path to the table")
    @Macro
    public String tableName;

    @Description("Key field in the record")
    @Macro
    public String key;

    public MapRDBSinkConfig(String referenceName) {
      super(referenceName);
    }
  }
}
