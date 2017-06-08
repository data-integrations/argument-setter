package co.cask.plugin.argument;

/**
 *
 */
//public class DriverArgumentSetterTest {
//
//  @Test
//  public void testSetter() throws Exception {
//    MockActionContext mockActionContext = new MockActionContext();
//
//    Schema schema = Schema.recordOf("testRecord",
//                                    Schema.FieldDefinition.of("int", Schema.of(Schema.Type.INT)),
//                                    Schema.FieldDefinition.of("intN", Schema.nullableOf(Schema.of(Schema.Type.INT))),
//                                    Schema.FieldDefinition.of("long", Schema.of(Schema.Type.LONG)),
//                                    Schema.FieldDefinition.of("longN", Schema.nullableOf(Schema.of(Schema.Type.LONG))),
//                                    Schema.FieldDefinition.of("float", Schema.of(Schema.Type.FLOAT)),
//                                    Schema.FieldDefinition.of("floatN", Schema.nullableOf(Schema.of(Schema.Type.FLOAT))),
//                                    Schema.FieldDefinition.of("double", Schema.of(Schema.Type.DOUBLE)),
//                                    Schema.FieldDefinition.of("doubleN", Schema.nullableOf(Schema.of(Schema.Type.DOUBLE))),
//                                    Schema.FieldDefinition.of("string", Schema.of(Schema.Type.STRING)),
//                                    Schema.FieldDefinition.of("string", Schema.nullableOf(Schema.of(Schema.Type.STRING))),
//                                    Schema.FieldDefinition.of("arr", Schema.arrayOf(Schema.of(Schema.Type.STRING))),
//                                    Schema.FieldDefinition.of("arrN",
//                                                    Schema.arrayOf(Schema.nullableOf(Schema.of(Schema.Type.STRING)))));
//
//    DatasetInfo datasetInfo = DatasetInfo.fromSchema(schema);
//    Driver driver = Driver.from(",", "https://s3.amazonaws.com/path/to/dir", "file123");
//    DriverArgumentSetter driverArgumentSetter = new TestSetter(driver, datasetInfo);
//    driverArgumentSetter.run(mockActionContext);
//
//    SettableArguments args = mockActionContext.getArguments();
//
//    Assert.assertEquals("https://s3.amazonaws.com/path/to/dir/file123", args.get(DriverArgumentSetter.INPUT_PATH));
//    Assert.assertEquals(schema.toString(), args.get(DriverArgumentSetter.OUTPUT_SCHEMA));
//
//    StringBuilder directives = new StringBuilder("parse-as-csv body , false\ndrop body\nset columns ");
//    for (Schema.FieldDefinition field : schema.getFields()) {
//      directives.append(field.getName()).append(",");
//    }
//    directives.deleteCharAt(directives.length() - 1);
//    directives.append("\n");
//    Assert.assertEquals(directives.toString(), args.get(DriverArgumentSetter.DIRECTIVES));
//  }
//
//  /**
//   * Overrides logic that makes an HTTP call to get the nebula json to instead return a predefined object.
//   */
//  private static class TestSetter extends DriverArgumentSetter {
//    private static final Gson GSON = new Gson();
//    private final Driver driver;
//    private final DatasetInfo datasetInfo;
//
//    public TestSetter(Driver driver, DatasetInfo datasetInfo) {
//      super(new HTTPConfig());
//      this.driver = driver;
//      this.datasetInfo = datasetInfo;
//    }
//
//    @Override
//    public void run(ActionContext context) throws Exception {
//      handleResponse(context, GSON.toJson(driver));
//    }
//
//    @Override
//    protected DatasetInfo getDatasetInfo(NebulaRegistry nebulaRegistry) throws IOException {
//      return datasetInfo;
//    }
//  }
//}
