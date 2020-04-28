package innohack.gem.dao;

import innohack.gem.entity.rule.Group;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A basic group dao using everything in-memory
 *
 * @author TC
 */
@Repository
public class GroupDao implements IGroupDao {
  public static HashMap<String, Group> featureStore = new HashMap<String, Group>();

  @Override
  public List<Group> getGroups() {
    return new ArrayList(featureStore.values());
  }

  @Override
  public Group getGroup(String groupName) {
    return featureStore.get(groupName);
  }

  @Override
  public boolean updateGroupName(String oldGroupName, String newGroupName) {
    if (featureStore.containsKey(oldGroupName)) {
      Group existingGroup = featureStore.get(oldGroupName);
      featureStore.remove(oldGroupName);
      existingGroup.setName(newGroupName);
      featureStore.put(existingGroup.getName(), existingGroup);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean saveGroup(Group group) {
    featureStore.put(group.getName(), group);
    return true;
  }

  @Override
  public boolean deleteGroup(String groupName) {
    featureStore.remove(groupName);
    return true;
  }
}
