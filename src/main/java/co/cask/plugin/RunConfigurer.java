/*
 * Copyright © 2016 Cask Data, Inc.
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

import co.cask.cdap.api.TxRunnable;
import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Macro;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.common.Bytes;
import co.cask.cdap.api.data.DatasetContext;
import co.cask.cdap.api.dataset.lib.PartitionedFileSet;
import co.cask.cdap.api.dataset.table.Row;
import co.cask.cdap.api.dataset.table.Scan;
import co.cask.cdap.api.dataset.table.Scanner;
import co.cask.cdap.api.dataset.table.Table;
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.action.Action;
import co.cask.cdap.etl.api.action.ActionContext;
import co.cask.cdap.etl.api.action.SettableArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * Configurer for the pipeline run/
 */
@Plugin(type = Action.PLUGIN_TYPE)
@Name("RunConfigurer")
public class RunConfigurer extends Action {
  private static final String INCOMPLETE_DATASET_NAME = "incomplete";
  private static final Logger LOG = LoggerFactory.getLogger(RunConfigurer.class);
  private final Config config;

  public RunConfigurer(Config conf) {
    this.config = conf;
  }

  @Override
  public void run(ActionContext actionContext) throws Exception {
    final SettableArguments arguments = actionContext.getArguments();
    final String type = arguments.get(config.typeField);
    if (type != null) {
      return;
    }
    actionContext.execute(new TxRunnable() {
      @Override
      public void run(DatasetContext datasetContext) throws Exception {
        Table table = datasetContext.getDataset(INCOMPLETE_DATASET_NAME);
        if (table == null) {
          LOG.info ("Table does not exist {}", INCOMPLETE_DATASET_NAME);
          return;
        }
        try (Scanner scan = table.scan(new Scan(null, null))) {
          Row next = scan.next();
          if (next == null) {
            // Nothing incomplete
            return;
          }
          byte[] file_path = next.getRow();
          byte[] member_id = next.get("member_id");
          byte[] file_type = next.get("file_type");
          LOG.info ("member_id {}", Bytes.toString(member_id));
          LOG.info ("file_type {}", Bytes.toString(file_type));
          LOG.info ("file_path {}", Bytes.toString(file_path));
          arguments.set("member.id", Bytes.toString(member_id));
          arguments.set("file.type", Bytes.toString(file_type));
          arguments.set("file.path", Bytes.toString(file_path));
          table.delete(file_path);
        }
      }
    });
  }

  public static class Config extends PluginConfig {

    @Description("Runtime argument name which contains the type field.")
    @Macro
    @Nullable
    private String typeField;

    public Config() {
    }
  }
}
