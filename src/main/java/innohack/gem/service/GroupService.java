package innohack.gem.service;

import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.rule.Group;
import java.util.List;
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
    matcherService.onUpdateGroupRule(group);
    return result;
  }

  public Group saveGroup(Group group) {
    group = groupDao.saveGroup(group);
    matcherService.onUpdateGroupRule(group);
    return group;
  }
}
