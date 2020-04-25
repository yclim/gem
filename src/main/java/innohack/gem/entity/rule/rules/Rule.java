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
public abstract class Rule {

  private String label;
  private RuleType ruleType;
  private String name;
  private List<Parameter> params;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public RuleType getRuleType() {
    return ruleType;
  }

  public void setRuleType(RuleType ruleType) {
    this.ruleType = ruleType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

  public abstract boolean check(GEMFile gemFile);
}
