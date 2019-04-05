package io.cdap.proto;

import com.google.gson.Gson;
import io.cdap.plugin.proto.Argument;
import io.cdap.plugin.proto.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests {@link Configuration}
 */
public class ConfigurationTest {
  private static final String CONFIG="{\n" +
    "    \"arguments\" : [\n" +
    "           { \"name\" : \"input.path\", \"type\" : \"string\", \"value\" : \"/tmp/data/json/1.json\" },\n" +
    "           { \"name\" : \"output.path\", \"type\" : \"string\", \"value\" : \"output_demo_1\" },\n" +
    "           { \"name\" : \"directive.parse\", \"type\" : \"array\", \"value\" : [ \"parse-as-json body\", \"columns-replace s/body_//g\" ] },\n" +
    "           { \"name\" : \"directive.schema\", \"type\" : \"schema\", \"value\" : [ { \"name\" : \"text\", \"type\" : \"string\", \"nullable\" : true } ] },\n" +
    "           { \"name\" : \"validation\", \"type\" : \"array\", \"value\" : [ \"foo1\", \"foo2\" ] }\n" +
    "        ]\n" +
    "}";

  @Test
  public void testConfigParsing() throws Exception {
    Gson gson = new Gson();
    Configuration configuration = gson.fromJson(CONFIG, Configuration.class);
    List<Argument> arguments = configuration.getArguments();

    Assert.assertNotNull(configuration);

    Assert.assertEquals("input.path", arguments.get(0).getName());
    Assert.assertEquals("/tmp/data/json/1.json", arguments.get(0).getValue());

    Assert.assertEquals("output.path", arguments.get(1).getName());
    Assert.assertEquals("output_demo_1", arguments.get(1).getValue());

    Assert.assertEquals("directive.parse", arguments.get(2).getName());
    Assert.assertEquals("parse-as-json body\ncolumns-replace s/body_//g", arguments.get(2).getValue());

    Assert.assertEquals("directive.schema", arguments.get(3).getName());
    Assert.assertEquals("{\"type\":\"record\",\"name\":\"record\",\"fields\":[{\"name\":\"text\",\"type\":[\"string\",\"null\"]}]}", arguments.get(3).getValue());

    Assert.assertEquals("validation", arguments.get(4).getName());
    Assert.assertEquals("foo1\nfoo2", arguments.get(4).getValue());
  }

}
