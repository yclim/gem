package innohack.gem.dao;

import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Repository;

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
    Rule fileExtensionRule = new FileExtension();
    fileExtensionRule.setName("fer-1");
    fileExtensionRule.getParams().get(0).setValue("xls");
    Group group = new Group();
    group.setName("mock group 1");
    group.setMatchedCount(100);
    group.setRules(Arrays.asList(fileExtensionRule));

    return Arrays.asList(group);
    // return featureStore.values();
  }

  @Override
  public Group getGroup(String groupName) {
    return featureStore.get(groupName);
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

  /*
  private List<Group> mockGroups() {
  	Group g = new Group();
  	g.setGroupName("test");
  	return Lists.newArrayList(g);
  }
  */
}
