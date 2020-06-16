package innohack.gem.core.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "ruleId")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FileExtension.class),
  @JsonSubTypes.Type(value = CsvHeaderColumnValue.class)
})
public abstract class Rule implements Comparable<Rule> {

  private String label;
  private RuleType ruleType;
  private String name;
  private List<Parameter> params;

  @Override
  public int compareTo(Rule rule) {
    if (this.equals(rule)) {
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
    if (!(o instanceof Rule)) return false;
    Rule rule = (Rule) o;
    if (!this.getClass().getCanonicalName().equals(rule.getClass().getCanonicalName())) {
      return false;
    }
    /*
    if (this.params.size() != rule.getParams().size()) {
      return false;
    }*/
    return label.equals(rule.label)
        && ruleType == rule.ruleType
        && Objects.equals(name, rule.name)
        && params.equals(rule.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, ruleType, name, params, this.getClass().getCanonicalName());
  }
  /**
   * Use for render UI Label
   *
   * @return
   */
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Use to organize rule in UI
   *
   * @return RuleType
   */
  public RuleType getRuleType() {
    return ruleType;
  }

  public void setRuleType(RuleType ruleType) {
    this.ruleType = ruleType;
  }

  /**
   * Unique identifier for each rule instance
   *
   * @return
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * list of parameter use for the rule
   *
   * @return
   */
  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

  public abstract boolean check(GEMFile gemFile);
}
