package innohack.gem.dao;

import java.util.Collection;

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

	@Override
	public Collection<Group> getGroups() {
		return mockGroups();
	}
	
	private Collection<Group> mockGroups() {
		Group g = new Group();
		g.setId("test");
		return Lists.newArrayList(g);
	}
	
}
