package innohack.gem.service;

import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;
import java.util.List;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  @Autowired private IGroupDao groupDao;
  @Autowired private MatchService matcherService;

  public List<Group> getGroups() {
    return groupDao.getGroups();
  }

  public Group getGroup(String groupName) {
    return groupDao.getGroup(groupName);
  }

  public boolean deleteGroup(String groupName) {
    Group group = new Group();
    group.setName(groupName);
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
      }
    }
  }
}
