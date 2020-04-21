package innohack.gem.entity.rule;

public class Parameter {

  private String label;
  private String placeholder;
  private String value;
  private ParamType type;

  public Parameter() {}

  public Parameter(String label, String placeholder, ParamType type) {
    this.label = label;
    this.placeholder = placeholder;
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ParamType getType() {
    return type;
  }

  public void setType(ParamType type) {
    this.type = type;
  }
}
