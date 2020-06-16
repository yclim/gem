package innohack.gem.dao;

import innohack.gem.core.entity.rule.Group;
import innohack.gem.database.RocksDatabase;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRockDao implements IGroupDao {
  private static final String DB_NAME = "Group";
  private RocksDatabase<String, Group> groupDb;
  private RocksDatabase<Integer, String> groupIdDb;

  public GroupRockDao() {
    groupDb = RocksDatabase.getInstance(DB_NAME, String.class, Group.class);
    groupIdDb = RocksDatabase.getInstance(DB_NAME + "_ID", Integer.class, String.class);
  }

  @Override
  public List<Group> getGroups() {
    return groupDb.getValues();
  }

  @Override
  public Map<Integer, String> getGroupIds() {
    return groupIdDb.getKeyValues();
  }

  @Override
  public Group getGroup(int groupId) {
    String groupName = (String) groupIdDb.get(groupId);
    return (Group) groupDb.get(groupName);
  }

  @Override
  public Group getGroup(String groupName) {
    return (Group) groupDb.get(groupName);
  }

  @Override
  public boolean updateGroupName(String oldGroupName, String newGroupName) {
    if (getGroup(newGroupName) == null) {
      Group existingGroup = getGroup(oldGroupName);
      if (existingGroup != null) {
        groupDb.delete(oldGroupName);
        existingGroup.setName(newGroupName);
        groupDb.put(existingGroup.getName(), existingGroup);
        groupIdDb.put(existingGroup.getGroupId(), existingGroup.getName());
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
    if (groupDb.deleteAll()) {
      groupIdDb.deleteAll();
      return true;
    }
    return false;
  }

  @Override
  public boolean deleteGroupsByName(List<String> groupNames) {
    Map<String, Group> map = groupDb.getKeyValues();
    List<Integer> ids = new ArrayList<Integer>();
    for (String groupName : map.keySet()) {
      ids.add(map.get(groupName).getGroupId());
    }
    if (groupDb.delete(groupNames)) {
      groupIdDb.delete(ids);
      return true;
    }
    return false;
  }

  @Override
  public boolean deleteGroupsById(List<Integer> groupIds) {
    Collection<String> names = groupIdDb.getKeyValues(groupIds).values();
    if (groupDb.delete(names)) {
      groupIdDb.delete(groupIds);
      return true;
    }
    return false;
  }

  private int getNextIncrementId() {
    List<Integer> ids = groupIdDb.getKeys();
    if (ids.size() == 0) {
      return 1;
    }
    int i = Collections.max(ids);
    return i + 1;
  }

  @Override
  public Group saveGroup(Group group) {
    Group existingGroup = getGroup(group.getName());
    if (existingGroup != null) {
      group.setGroupId(existingGroup.getGroupId());
    } else {
      group.setGroupId(getNextIncrementId());
    }
    groupDb.put(group.getName(), group);
    groupIdDb.put(group.getGroupId(), group.getName());
    return group;
  }

  @Override
  public void saveGroups(Map<String, Group> map) {
    HashMap<Integer, String> groupIds = new HashMap<Integer, String>();
    for (String key : map.keySet()) {
      Group grp = map.get(key);
      groupIds.put(grp.getGroupId(), key);
    }
    groupDb.putHashMap(map);
    groupIdDb.putHashMap(groupIds);
  }

  @Override
  public boolean deleteGroup(int groupId) {
    String groupName = (String) groupIdDb.get(groupId);
    groupIdDb.delete(groupId);
    return groupDb.delete(groupName);
  }

  @Override
  public boolean deleteGroup(String groupName) {
    Group group = getGroup(groupName);
    groupIdDb.delete(group.getGroupId());
    return groupDb.delete(groupName);
  }
}
