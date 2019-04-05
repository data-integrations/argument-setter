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

package io.cdap.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.etl.api.action.Action;
import io.cdap.cdap.etl.api.action.ActionContext;
import io.cdap.plugin.http.HTTPArgumentSetter;
import io.cdap.plugin.http.HTTPConfig;
import io.cdap.plugin.proto.Argument;
import io.cdap.plugin.proto.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOG = LoggerFactory.getLogger(ArgumentSetter.class);
  private static final Gson gson = new Gson();

  public ArgumentSetter(HTTPConfig conf) {
    super(conf);
  }

  @Override
  protected void handleResponse(ActionContext context, String body) throws IOException {
    try {
      Configuration configuration = gson.fromJson(body, Configuration.class);
      for (Argument argument : configuration.getArguments()) {
        String name = argument.getName();
        String value = argument.getValue();
        if (value != null) {
          context.getArguments().set(name, value);
        } else {
          throw new RuntimeException("Configuration '" + name + "' is null. Cannot set argument to null.");
        }
      }
    } catch (JsonSyntaxException e) {
      throw new RuntimeException(String.format("Could not parse response from '%s': %s",
                                               conf.getUrl(), e.getMessage()));
    }
  }
}
