package innohack.gem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.GroupExportMixin;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;

@Service
public class GroupService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

  @Autowired private IGroupDao groupDao;
  @Autowired private MatchService matcherService;
  
  public List<Group> getGroups() {
    List<Group> group = groupDao.getGroups();
    Collections.sort(group);
    return group;
  }

  public Group getGroup(int groupId) {
    return groupDao.getGroup(groupId);
  }

  public Group getGroup(String groupName) {
    return groupDao.getGroup(groupName);
  }

  public boolean updateGroupName(String oldGroupName, String newGroupName) {
    return groupDao.updateGroupName(oldGroupName, newGroupName);
  }

  public boolean deleteGroup(int groupId) {
    Group group = new Group();
    group.setGroupId(groupId);
    List<Rule> rules = new ArrayList<>();
    group.setRules(rules);
    boolean result = groupDao.deleteGroup(groupId);
    matcherService.onUpdateEvent(group);
    return result;
  }

  public boolean deleteGroup(String groupName) {
    Group group = new Group();
    group.setName(groupName);
    List<Rule> rules = new ArrayList<>();
    group.setRules(rules);
    boolean result = groupDao.deleteGroup(groupName);
    matcherService.onUpdateEvent(group);
    return result;
  }

  public Group saveGroup(Group group) {
    group = groupDao.saveGroup(group);
    matcherService.onUpdateEvent(group);
    return group;
  }

  public static final String DEFAULT_FILEEXT_RULENAME_PREFIX = "File Extension";

  public void createDefaultGroup(List<GEMFile> files) {
    for (GEMFile file : files) {
      String extension = file.getExtension().toUpperCase();
      String defaultGroupName = extension;
      String defaultRuleName = extension + " " + GroupService.DEFAULT_FILEEXT_RULENAME_PREFIX;
      Group default_extension_group = groupDao.getGroup(defaultGroupName);
      if (default_extension_group == null) {
        default_extension_group = new Group();
        default_extension_group.setName(defaultGroupName);
        Rule rule = new FileExtension(file.getExtension());
        rule.setName(defaultRuleName);
        default_extension_group.setRules(Lists.newArrayList(rule));
        groupDao.saveGroup(default_extension_group);
        matcherService.onUpdateEvent(default_extension_group);
      }
    }
  }

  public List<Group> importGroups(byte[] data) throws IOException {
    LOGGER.info("Importing groups...");
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(data, new TypeReference<List<Group>>(){});
  }

  public byte[] exportGroups() throws IOException {
    LOGGER.info("Exporting the groups as json...");
    try {
      List<Group> groups = groupDao.getGroups();
      ObjectMapper mapper = new ObjectMapper();
      return mapper.addMixIn(Group.class, GroupExportMixin.class).
          writerWithDefaultPrettyPrinter().writeValueAsBytes(groups);
    } catch(IOException ex) {
      LOGGER.error("Error in exporting groups", ex);
      throw ex;
    }
  }
  
}
