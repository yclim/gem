package innohack.gem.core.entity.rule;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.rules.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group implements Comparable<Group> {
  private int groupId;
  private String name;
  private List<Rule> rules;
  private List<GEMFile> matchedFile = new ArrayList<GEMFile>();
  private ExtractConfig extractConfig;

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

  public ExtractConfig getExtractConfig() {
    return extractConfig;
  }

  public void setExtractConfig(ExtractConfig extractConfig) {
    this.extractConfig = extractConfig;
  }

  public List<GEMFile> getMatchedFile() {
    Collections.sort(matchedFile);
    return matchedFile;
  }

  public void setMatchedFile(List<GEMFile> matchedFile) {
    this.matchedFile = matchedFile;
  }

  @Override
  public int compareTo(Group group) {
    return this.getName().compareTo(group.name);
  }
}
