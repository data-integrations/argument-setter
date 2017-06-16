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

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.etl.api.action.Action;
import co.cask.cdap.etl.api.action.ActionContext;
import co.cask.plugin.http.HTTPArgumentSetter;
import co.cask.plugin.http.HTTPConfig;
import co.cask.plugin.proto.Argument;
import co.cask.plugin.proto.Configuration;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

/**
 * Generic argument setter for the pipeline based on retrieval of configuration from an HTTP endpoint.
 *
 * Following is JSON configuration that can be provided.
 *
 * <code>
 *   {
 *     "arguments" : [
 *        { "name" : "input.path", "type" : "string", "value" : "/data/sunny_feeds/master"},
 *        { "name" : "parse.schema, "type" : "schema", "value" : [
 *             { "name" : "fname", "type" : "string", "nullable" : true },
 *             { "name" : "age", "type" : "int", "nullable" : true},
 *             { "name" : "salary", "type" : "float", "nullable" : false}
 *          ]},
 *        { "name" : "directives", "type" : "array", "value" : [
 *             "parse-as-json body",
 *             "columns-replace s/body_//g",
 *             "keep f1,f2"
 *          ]}
 *     ]
 *   }
 * </code>
 */
@Plugin(type = Action.PLUGIN_TYPE)
@Name("ArgumentSetter")
@Description("Argument setter for dynamically configuring pipeline.")
public final class ArgumentSetter extends HTTPArgumentSetter<HTTPConfig> {
  private static final Gson gson = new Gson();

  public ArgumentSetter(HTTPConfig conf) {
    super(conf);
  }

  @Override
  protected void handleResponse(ActionContext context, String body) throws IOException {
    try {
      Configuration configuration = gson.fromJson(body, Configuration.class);
      for (Argument argument : configuration.getArguments()) {
        context.getArguments().set(argument.getName(), argument.getValue());
      }
    } catch (JsonSyntaxException e) {
      throw new RuntimeException(String.format("Could not parse response from '%s': %s",
                                               conf.getUrl(), e.getMessage()));
    }
  }
}
