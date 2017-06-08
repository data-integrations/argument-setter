package co.cask.plugin.argument.equifax.proto;

/**
 * Class description here.
 */
public class FieldDefinition {
  private final String name;
  private final String type;
  private final Boolean nullable;

  public FieldDefinition(String name, String type, Boolean nullable) {
    this.name = name;
    this.type = type;
    this.nullable = nullable;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Boolean getNullable() {
    return nullable;
  }
}
