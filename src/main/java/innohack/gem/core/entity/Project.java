package innohack.gem.core.entity;

import innohack.gem.core.entity.rule.Group;
import java.util.List;

public class Project {

  public static final String SPEC_VERSION = "1.0.0";

  private String specVersion;

  private List<Group> groups;

  private String projectName;

  public String getSpecVersion() {
    return specVersion;
  }

  public void setSpecVersion(String specVersion) {
    this.specVersion = specVersion;
  }

  public List<Group> getGroups() {
    return groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
}
