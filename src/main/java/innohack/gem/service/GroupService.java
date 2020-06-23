package innohack.gem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.entity.rule.GroupExportMixin;
import innohack.gem.core.rules.FileExtension;
import innohack.gem.core.rules.Rule;
import innohack.gem.dao.IGroupDao;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

  @Autowired private IGroupDao groupDao;
  @Autowired private MatchService matchService;
  @Autowired private ExtractService extractService;

  public List<Group> getGroups() {
    List<Group> group = groupDao.getGroups();
    Collections.sort(group);
    return group;
  }

  public Group getGroup(int groupId) {
    LOGGER.info("Fetching group: {}", groupId);
    return groupDao.getGroup(groupId);
  }

  public Group getGroup(String groupName) {
    return groupDao.getGroup(groupName);
  }

  public boolean updateGroupName(String oldGroupName, String newGroupName) {
    boolean result = groupDao.updateGroupName(oldGroupName, newGroupName);
    Group group = groupDao.getGroup(newGroupName);
    matchService.onUpdateEvent(group);
    return result;
  }

  public boolean deleteGroup(int groupId) {
    Group group = groupDao.getGroup(groupId);
    List<Rule> rules = new ArrayList<>();
    group.setRules(rules);
    boolean result = groupDao.deleteGroup(groupId);
    matchService.onUpdateEvent(group);
    return result;
  }

  public boolean deleteGroup(String groupName) {
    Group group = groupDao.getGroup(groupName);
    List<Rule> rules = new ArrayList<>();
    group.setRules(rules);
    boolean result = groupDao.deleteGroup(groupName);
    matchService.onUpdateEvent(group);
    return result;
  }

  public Group saveGroup(Group group) {
    group = groupDao.saveGroup(group);
    matchService.onUpdateEvent(group);
    return group;
  }

  public static final String DEFAULT_FILEEXT_RULENAME_PREFIX = "FE";

  public void createDefaultGroup(Collection<GEMFile> files) {
    int counter = 1;
    for (GEMFile file : files) {
      String extension = file.getExtension();
      String defaultGroupName = extension + "-" + "group";
      String defaultRuleName = GroupService.DEFAULT_FILEEXT_RULENAME_PREFIX + "-" + counter;
      Group default_extension_group = groupDao.getGroup(defaultGroupName);
      if (default_extension_group == null) {
        default_extension_group = new Group();
        default_extension_group.setName(defaultGroupName);
        Rule rule = new FileExtension(file.getExtension());
        rule.setName(defaultRuleName);
        default_extension_group.setRules(Lists.newArrayList(rule));
        groupDao.saveGroup(default_extension_group);
        matchService.onUpdateEvent(default_extension_group);
        counter++;
      }
    }
  }

  public List<Group> importProject(byte[] data) throws IOException {
    LOGGER.debug("Importing project...");
    ObjectMapper mapper = new ObjectMapper();
    List<Group> groups = mapper.readValue(data, Project.class).getGroups();
    List<Group> existingGroups = groupDao.getGroups();
    groupDao.deleteAll();
    existingGroups.stream()
        .forEach(
            group -> {
              matchService.onUpdateEvent(group);
            });
    Map<String, Group> map = new HashMap<String, Group>();
    groups.stream()
        .forEach(
            group -> {
              map.put(group.getName(), group);
            });
    groupDao.saveGroups(map);
    groups.stream()
        .forEach(
            group -> {
              matchService.onUpdateEvent(group);
              extractService.updateExtractConfig(group.getGroupId(), group.getExtractConfig());
            });
    return groups;
  }

  public byte[] exportProject(String version, String name) throws IOException {
    LOGGER.debug("Exporting the project as json...");
    try {
      List<Group> groups = groupDao.getGroups();
      groups.stream()
          .forEach(
              group -> {
                group.setExtractConfig(extractService.getExtractConfig(group.getGroupId()));
              });
      Project project = new Project();
      project.setGroups(groups);
      project.setSpecVersion(Project.SPEC_VERSION);
      if (version != null && version.length() > 0) project.setSpecVersion(version);
      if (name != null && name.length() > 0) project.setProjectName(name);
      ObjectMapper mapper = new ObjectMapper();
      return mapper
          .addMixIn(Group.class, GroupExportMixin.class)
          .writerWithDefaultPrettyPrinter()
          .writeValueAsBytes(project);
    } catch (IOException ex) {
      LOGGER.error("Error in exporting project", ex);
      throw ex;
    }
  }
}
