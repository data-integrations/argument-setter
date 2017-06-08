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

package co.cask.plugin.argument.equifax;

import co.cask.cdap.api.annotation.Description;
import co.cask.cdap.api.annotation.Name;
import co.cask.cdap.api.annotation.Plugin;
import co.cask.cdap.api.data.schema.Schema;
import co.cask.cdap.etl.api.action.Action;
import co.cask.cdap.etl.api.action.ActionContext;
import co.cask.plugin.argument.HTTPArgumentSetter;
import co.cask.plugin.argument.HTTPConfig;
import co.cask.plugin.argument.equifax.proto.FieldDefinition;
import co.cask.plugin.argument.equifax.proto.Source;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sets pipeline arguments based on a driver json.
 */
@Plugin(type = Action.PLUGIN_TYPE)
@Name("DataFactoryDriver")
@Description("Data Factory Driver for controlling pipeline configurations dynamically.")
public class DataFactoryDriver extends HTTPArgumentSetter<HTTPConfig> {
  private static final Logger LOG = LoggerFactory.getLogger(DataFactoryDriver.class);
  private static final Gson GSON = new Gson();
  static final String PARSER_DIRECTIVES = "parser.directives";
  static final String VALIDATION_DIRECTIVES = "validation.directives";
  static final String INPUT_PATH = "input.path";
  static final String OUTPUT_PATH = "output.path";
  static final String SCHEMA = "output.schema";

  public DataFactoryDriver(HTTPConfig conf) {
    super(conf);
  }

  @Override
  protected void handleResponse(ActionContext context, String responseBody) throws IOException {
    Source source;
    try {
      source = GSON.fromJson(responseBody, Source.class);
    } catch (JsonSyntaxException e) {
      throw new RuntimeException(String.format("Could not parse response from '%s': %s",
                                               conf.getUrl(), e.getMessage()));
    }

    context.getArguments().set(INPUT_PATH, source.getInput());
    context.getArguments().set(OUTPUT_PATH, source.getOutput());
    context.getArguments().set(PARSER_DIRECTIVES, Joiner.on("\n").join(source.getParser()));
    context.getArguments().set(VALIDATION_DIRECTIVES, Joiner.on("\n").join(source.getValidation()));
    context.getArguments().set(SCHEMA, getSchema(source).toString());
  }

  private Schema getSchema(Source source) {
    List<FieldDefinition> fields = source.getSchema();
    List<Schema.Field> flds = new ArrayList<>();
    for (FieldDefinition field : fields) {
      Schema.Type type = Schema.Type.STRING;
      if (field.getType().equalsIgnoreCase("double")) {
        type = Schema.Type.DOUBLE;
      } else if (field.getType().equalsIgnoreCase("float")) {
        type = Schema.Type.FLOAT;
      } else if (field.getType().equalsIgnoreCase("long")) {
        type = Schema.Type.LONG;
      } else if (field.getType().equalsIgnoreCase("int")) {
        type = Schema.Type.INT;
      }

      Schema s;
      if (field.getNullable()) {
        s = Schema.nullableOf(Schema.of(type));
      } else {
        s = Schema.of(type);
      }
      Schema.Field fld = Schema.Field.of(field.getName(), s);
      flds.add(fld);
    }
    return Schema.recordOf(source.getName(), flds);
  }
}
