package innohack.gem.entity.rule;

import innohack.gem.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.List;

public class Group implements Comparable<Group> {
  private int groupId;
  private String name;
  private List<Rule> rules;
  private int matchedCount;
  private List<String> matchedFile = new ArrayList<String>();

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

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
    return matchedFile.size();
  }

  public void setMatchedCount(int matchedCount) {
    this.matchedCount = matchedCount;
  }

  public List<String> getMatchedFile() {
    return matchedFile;
  }

  public void setMatchedFile(List<String> matchedFile) {
      this.matchedFile = matchedFile;
  }

  @Override
  public int compareTo(Group group) {
    return this.getName().compareTo(group.name);
  }
}
