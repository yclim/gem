package innohack.gem.entity.rule.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import java.util.List;

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
      return -1;
    }
  }

  @Override
  public boolean equals(Object obj) {
    Rule rule = (Rule) obj;
    if (!this.getClass().getCanonicalName().equals(rule.getClass().getCanonicalName())) {
      return false;
    }

    if (this.params.size() != rule.getParams().size()) {
      return false;
    }
    for (Parameter param : rule.getParams()) {
      if (!this.params.contains(param)) {
        return false;
      }
    }
    if (!this.label.equals(rule.getLabel())) {
      return false;
    }
    if (this.ruleType != rule.getRuleType()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    String hash = "";
    for (Parameter para : this.params) {
      hash = hash + para.hashCode() + "|";
    }
    hash = hash + this.getClass().getCanonicalName() + "|";
    hash = hash + this.label + "|";
    hash = hash + this.name + "|";
    hash = hash + this.ruleType;

    return hash.hashCode();
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
