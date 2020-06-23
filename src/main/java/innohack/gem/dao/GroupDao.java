package innohack.gem.dao;

import innohack.gem.core.entity.rule.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * A basic group dao using everything in-memory
 *
 * @author TC
 */
@Repository
public class GroupDao implements IGroupDao {
  public static int maxKey = 0;
  public static ConcurrentHashMap<String, Group> featureStore =
      new ConcurrentHashMap<String, Group>();
  public static ConcurrentHashMap<Integer, String> featureStoreId =
      new ConcurrentHashMap<Integer, String>();

  private static int getIncrementId() {
    maxKey = maxKey + 1;
    return maxKey;
  }

  @Override
  public List<Group> getGroups() {
    return new ArrayList<Group>(featureStore.values());
  }

  @Override
  public Map<Integer, String> getGroupIds() {
    return featureStoreId;
  }

  @Override
  public Group getGroup(int groupId) {
    for (Group group : featureStore.values()) {
      if (group.getGroupId() == groupId) {
        return group;
      }
    }
    return null;
  }

  @Override
  public Group getGroup(String groupName) {
    return featureStore.get(groupName);
  }

  @Override
  public boolean updateGroupName(String oldGroupName, String newGroupName) {
    if (!featureStore.containsKey(newGroupName)) {
      if (featureStore.containsKey(oldGroupName)) {
        Group existingGroup = featureStore.get(oldGroupName);
        featureStore.remove(oldGroupName);
        existingGroup.setName(newGroupName);
        featureStore.put(existingGroup.getName(), existingGroup);
        featureStoreId.put(existingGroup.getGroupId(), existingGroup.getName());
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean deleteAll() {
    featureStore.clear();
    return true;
  }

  @Override
  public boolean deleteGroupsByName(List<String> groupNames) {
    for (String name : groupNames) {
      delete(name);
    }
    return true;
  }

  @Override
  public boolean deleteGroupsById(List<Integer> groupIds) {
    for (Integer id : groupIds) {
      deleteGroup(id);
    }
    return true;
  }

  @Override
  public Group saveGroup(Group group) {
    Group existingGroup = featureStore.get(group.getName());
    if (existingGroup != null) {
      group.setGroupId(existingGroup.getGroupId());
    } else {
      group.setGroupId(getIncrementId());
    }
    featureStore.put(group.getName(), group);
    featureStoreId.put(group.getGroupId(), group.getName());
    return group;
  }

  @Override
  public void saveGroups(Map<String, Group> map) {
    for (String key : map.keySet()) {
      featureStore.put(key, map.get(key));
      featureStoreId.put(map.get(key).getGroupId(), map.get(key).getName());
    }
  }

  @Override
  public boolean deleteGroup(int groupId) {
    String groupName = featureStoreId.get(groupId);
    return delete(groupName);
  }

  @Override
  public boolean deleteGroup(String groupName) {
    return delete(groupName);
  }

  private static boolean delete(String groupName) {
    if (featureStore.containsKey(groupName)) {
      featureStore.remove(groupName);
      for (int id : featureStoreId.keySet()) {
        if (featureStoreId.get(id).equals(groupName)) {
          featureStoreId.remove(id);
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
