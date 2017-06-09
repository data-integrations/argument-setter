package co.cask.plugin.argument.proto;

import java.util.List;

/**
 * Defines each individual sources.
 */
public class Source {
  private final String title;
  private final String description;
  private final String id;
  private final String name;
  private final String source_type;
  private final String file_format;
  private final String delimiter;
  private final String header_rows;
  private final String input;
  private final String output;

  public List<FieldDefinition> getSchema() {
    return schema;
  }

  private final List<FieldDefinition> schema;

  public List<String> getParser() {
    return parser;
  }

  public List<String> getValidation() {
    return validation;
  }

  private final List<String> parser;
  private final List<String> validation;
  private final boolean isActive;

  public Source(String title, String description, String id, String name,
                String source_type, String file_format, String delimiter,
                String header_rows, String input, String output,
                List<String> parser, List<String> validation, List<FieldDefinition> schema, boolean isActive) {
    this.title = title;
    this.description = description;
    this.id = id;
    this.name = name;
    this.source_type = source_type;
    this.file_format = file_format;
    this.delimiter = delimiter;
    this.header_rows = header_rows;
    this.input = input;
    this.output = output;
    this.parser = parser;
    this.validation = validation;
    this.isActive = isActive;
    this.schema = schema;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSource_type() {
    return source_type;
  }

  public String getFile_format() {
    return file_format;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public String getHeader_rows() {
    return header_rows;
  }

  public boolean isActive() {
    return isActive;
  }

  public String getInput() {
    return input;
  }

  public String getOutput() {
    return output;
  }

}
