package innohack.gem.entity.rule;

import java.util.Objects;

public class Parameter implements Comparable<Parameter> {

  private String label;
  private String placeholder;
  private String value;
  private ParamType type;

  public Parameter() {}

  public Parameter(String label, String placeholder, ParamType type) {
    this(label, placeholder, type, null);
  }

  public Parameter(String label, String placeholder, ParamType type, String value) {
    this.label = label;
    this.placeholder = placeholder;
    this.type = type;
    this.value = value;
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

  @Override
  public int compareTo(Parameter parameter) {
    if (this.equals(parameter)) {
      return 0;
    } else {
      int result = this.label.compareTo(this.label);
      if (result != 0) {
        return result;
      }
      return -1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Parameter)) return false;
    Parameter parameter = (Parameter) o;
    return label.equals(parameter.label)
        && placeholder.equals(parameter.placeholder)
        && Objects.equals(value, parameter.value)
        && type == parameter.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, placeholder, value, type);
  }
}
