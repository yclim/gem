package innohack.gem.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.entity.rule.rules.FileExtension;
import innohack.gem.core.entity.rule.rules.FilenamePrefix;
import innohack.gem.core.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import(SkipRockDBConfig.class)
@ExtendWith(SpringExtension.class)
public class GroupTest {

  @Autowired private GroupService groupService;

  private Group ext_csv_group;
  private Group ext_dat_group;
  private Group prefix_d_group;

  public GroupTest() {
    this.ext_csv_group = new Group();
    ext_csv_group.setName("extension_csv_grouprule");
    Rule rule1 = new FileExtension("csv");
    Rule rule2 = new FilenamePrefix("chats");
    List<Rule> csv_group_rules = new ArrayList<Rule>();
    csv_group_rules.add(rule1);
    csv_group_rules.add(rule2);
    ext_csv_group.setRules(csv_group_rules);
    ext_dat_group = new Group();
    ext_dat_group.setName("extension_dat_grouprule");
    Rule rule3 = new FileExtension("dat");
    List<Rule> dat_group_rules2 = new ArrayList<Rule>();
    dat_group_rules2.add(rule3);
    ext_dat_group.setRules(dat_group_rules2);
    prefix_d_group = new Group();
    prefix_d_group.setName("prefix_da_grouprule");
    Rule rule4 = new FilenamePrefix("da");
    List<Rule> prefix_d_group_rules = new ArrayList<Rule>();
    prefix_d_group_rules.add(rule4);
    prefix_d_group.setRules(prefix_d_group_rules);
  }

  @Test
  public void testSaveGroup() {
    groupService.saveGroup(ext_csv_group);
    int id = ext_csv_group.getGroupId();
    String oldname = ext_csv_group.getName();
    Group g1 = groupService.getGroup(id);
    Group g2 = groupService.getGroup(oldname);
    assertTrue(g1.getName().equals(g2.getName()));
    assertTrue(g1.getGroupId() == g2.getGroupId());
    String newname = "ABC";
    groupService.updateGroupName(oldname, newname);
    groupService.saveGroup(ext_csv_group);
    assertTrue(groupService.getGroup(id).getName().equals(newname));
  }
}
