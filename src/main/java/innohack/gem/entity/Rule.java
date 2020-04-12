package innohack.gem.entity;

import innohack.gem.entity.gem.data.Target;
import java.util.Collection;

public class Rule {

  private String ruleId;

  private String label;

  private Collection<Parameter> params;

  private Target target; // XXX not sure how this is applied...

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Collection<Parameter> getParams() {
    return params;
  }

  public void setParams(Collection<Parameter> params) {
    this.params = params;
  }

  public Target getTarget() {
    return target;
  }

  public void setTarget(Target target) {
    this.target = target;
  }
}
