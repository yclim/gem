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

  Group getGroup(String groupName);

  boolean saveGroup(Group group);

  boolean deleteGroup(String groupName);

  boolean updateGroupName(String oldGroupName, String newGroupName);
}
