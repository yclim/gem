package innohack.gem.entity.rule;

import innohack.gem.entity.rule.rules.Rule;
import java.util.List;

public class Group {
  private String name;
  private List<Rule> rules;
  private int matchedCount;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Rule> getRules() {
    return rules;
  }

  public void setRules(List<Rule> rules) {
    this.rules = rules;
  }

  public int getMatchedCount() {
    return matchedCount;
  }

  public void setMatchedCount(int matchedCount) {
    this.matchedCount = matchedCount;
  }
}
