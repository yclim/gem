package innohack.gem.dao;

import innohack.gem.entity.rule.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * A basic group dao using everything in-memory
 *
 * @author TC
 */
@Repository
public class GroupDao implements IGroupDao {
  public static ConcurrentHashMap<String, Group> featureStore =
      new ConcurrentHashMap<String, Group>();

  @Override
  public List<Group> getGroups() {
    return new ArrayList(featureStore.values());
  }

  @Override
  public Group getGroup(String groupName) {
    return featureStore.get(groupName);
  }

  @Override
  public Group saveGroup(Group group) {
    featureStore.put(group.getName(), group);
    return group;
  }

  @Override
  public boolean deleteGroup(String groupName) {
    featureStore.remove(groupName);
    return true;
  }
}
