/*
 * Copyright © 2018 Cask Data, Inc.
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
import co.cask.cdap.api.plugin.PluginConfig;
import co.cask.cdap.etl.api.action.Action;
import co.cask.cdap.etl.api.action.ActionContext;
import com.google.common.base.Splitter;

import java.util.UUID;

/**
 * Generates arguments for the pipeline
 */
@Plugin(type = Action.PLUGIN_TYPE)
@Name("IDGenerator")
@Description("Generates UUIDs and sets them as arguments that can be used by other parts of the pipeline.")
public final class IDGenerator extends Action {
  private final Conf conf;

  public IDGenerator(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run(ActionContext actionContext) {
    for (String argument : Splitter.on(',').trimResults().omitEmptyStrings().split(conf.arguments)) {
      actionContext.getArguments().set(argument, UUID.randomUUID().toString());
    }
  }

  /**
   * Configuration for the pipeline.
   */
  public static class Conf extends PluginConfig {
    @Macro
    @Description("A unique ID is generated for each argument provided. Stages further down the pipeline can then "
      + "use these arguments through macros.")
    private String arguments;
  }
}
