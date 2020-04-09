package innohack.gem.dao;

import java.util.Collection;

import innohack.gem.entity.Group;

/**
 * Allow for injecting different DAO implementations...
 * e.g. in-memory, database, es, etc
 * @author TC
 *
 */
public interface IGroupDao {

	Collection<Group> getGroups();

	Group getGroup(String groupName);

	boolean saveGroup(Group group);

	boolean deleteGroup(String groupName);
	
}
