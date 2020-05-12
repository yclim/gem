package innohack.gem.dao;

import innohack.gem.entity.rule.Group;
import java.util.List;
import java.util.Map;

/**
 * Allow for injecting different DAO implementations... e.g. in-memory, database, es, etc
 *
 * @author TC
 */
public interface IGroupDao {

  List<Group> getGroups();

  Map<Integer, String> getGroupIds();

  Group getGroup(int groupId);

  Group getGroup(String groupName);

  Group saveGroup(Group group);

  void saveGroups(Map<String, Group> map);

  boolean deleteGroup(int groupId);

  boolean deleteGroup(String groupName);

  boolean updateGroupName(String oldGroupName, String newGroupName);

  boolean deleteAll();

  boolean deleteGroupsByName(List<String> groupNames);

  boolean deleteGroupsById(List<Integer> groupIds);
}
