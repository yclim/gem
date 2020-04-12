package innohack.gem.dao;

import java.util.Collection;
import java.util.HashMap;

import innohack.gem.entity.GEMFile;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

import innohack.gem.entity.Group;

/**
 * A basic group dao using everything in-memory
 * 
 * @author TC
 *
 */
@Repository
public class BasicGroupDao implements IGroupDao {
	public static HashMap<String, Group> featureStore = new HashMap<String, Group>();

	@Override
	public Collection<Group> getGroups() {
		return featureStore.values();
	}

	@Override
	public Group getGroup(String groupName) {
		return featureStore.get(groupName);
	}

	@Override
	public boolean saveGroup(Group group) {
		featureStore.put(group.getGroupName(), group);
		return true;
	}

	@Override
	public boolean deleteGroup(String groupName) {
		featureStore.remove(groupName);
		return true;
	}

	/*
	private Collection<Group> mockGroups() {
		Group g = new Group();
		g.setGroupName("test");
		return Lists.newArrayList(g);
	}
	*/
}
