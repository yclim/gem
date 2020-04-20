package innohack.gem.entity.rule;

import innohack.gem.entity.rule.rules.Rule;
import java.util.Collection;

public class Group {
  private String name;
  private Collection<Rule> rules;
  private int matchedCount;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<Rule> getRules() {
    return rules;
  }

  public void setRules(Collection<Rule> rules) {
    this.rules = rules;
  }

  public int getMatchedCount() {
    return matchedCount;
  }

  public void setMatchedCount(int matchedCount) {
    this.matchedCount = matchedCount;
  }
}
