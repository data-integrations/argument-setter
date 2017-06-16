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

package co.cask.plugin.proto;

import co.cask.cdap.api.data.schema.Schema;
import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class description here.
 */
public final class Argument {
  private String name;
  private String type;
  private JsonElement value;

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    if (type.equalsIgnoreCase("schema")) {
      return createSchema(value).toString();
    } else if (type.equalsIgnoreCase("int")) {
      return Integer.toString(value.getAsInt());
    } else if (type.equalsIgnoreCase("float")) {
      return Float.toString(value.getAsFloat());
    } else if (type.equalsIgnoreCase("double")) {
      return Double.toString(value.getAsDouble());
    } else if (type.equalsIgnoreCase("short")) {
      return Short.toString(value.getAsShort());
    } else if (type.equalsIgnoreCase("string")) {
      return value.getAsString();
    } else if (type.equalsIgnoreCase("char")) {
      return Character.toString(value.getAsCharacter());
    } else if (type.equalsIgnoreCase("array")) {
      List<String> values = new ArrayList<>();
      for (JsonElement v : value.getAsJsonArray()) {
        values.add(v.getAsString());
      }
      return Joiner.on("\n").join(values);
    } else if (type.equalsIgnoreCase("map")) {
      List<String> values = new ArrayList<>();
      for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
        values.add(String.format("%s:%s", entry.getKey(), entry.getValue().getAsString()));
      }
      return Joiner.on(",").join(values);
    }
    return value.getAsString();
  }

  private Schema createSchema(JsonElement array) {
    List<Schema.Field> fields = new ArrayList<>();
    for (JsonElement field : array.getAsJsonArray()) {
      Schema.Type type = Schema.Type.STRING;

      JsonObject object = field.getAsJsonObject();
      String fieldType = object.get("type").getAsString().toLowerCase();

      boolean isNullable = true;
      if (object.get("nullable") != null) {
        isNullable = object.get("nullable").getAsBoolean();
      }

      String name = object.get("name").getAsString();

      if (fieldType.equals("double")) {
        type = Schema.Type.DOUBLE;
      } else if (fieldType.equals("float")) {
        type = Schema.Type.FLOAT;
      } else if (fieldType.equals("long")) {
        type = Schema.Type.LONG;
      } else if (fieldType.equals("int")) {
        type = Schema.Type.INT;
      } else if (fieldType.equals("short")) {
        type = Schema.Type.INT;
      } else if (fieldType.equals("string")) {
        type = Schema.Type.STRING;
      }

      Schema nullable;
      if (isNullable) {
        nullable = Schema.nullableOf(Schema.of(type));
      } else {
        nullable = Schema.of(type);
      }
      Schema.Field fld = Schema.Field.of(name, nullable);
      fields.add(fld);
    }

    return Schema.recordOf("record", fields);
  }
}
