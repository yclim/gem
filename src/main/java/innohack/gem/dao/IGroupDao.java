package innohack.gem.dao;

import innohack.gem.entity.rule.Group;
import java.util.List;

/**
 * Allow for injecting different DAO implementations... e.g. in-memory, database, es, etc
 *
 * @author TC
 */
public interface IGroupDao {

  List<Group> getGroups();

  Group getGroup(int groupId);

  Group getGroup(String groupName);

  Group saveGroup(Group group);

  boolean deleteGroup(int groupId);

  boolean deleteGroup(String groupName);

  boolean updateGroupName(String oldGroupName, String newGroupName);
}
