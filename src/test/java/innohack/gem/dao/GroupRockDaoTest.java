package innohack.gem.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.FilenamePrefix;
import innohack.gem.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GroupRockDaoTest {

  private Group ext_csv_group;
  private Group ext_dat_group;
  private Group prefix_d_group;

  public GroupRockDaoTest() {
    this.ext_csv_group = new Group();
    ext_csv_group.setName("extension_csv_grouprule");
    Rule rule1 = new FileExtension("csv");
    Rule rule2 = new FilenamePrefix("reviews");
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
  public void testSaveGroup() throws Exception {
    GroupRockDao groupDao = new GroupRockDao();
    groupDao.saveGroup(ext_dat_group);
    Group group = groupDao.getGroup(ext_dat_group.getGroupId());
    assertTrue(group.getName().equals(ext_dat_group.getName()));
    assertTrue(group.getGroupId() == (ext_dat_group.getGroupId()));
    assertTrue(group.getRules().equals(ext_dat_group.getRules()));

    groupDao.saveGroup(ext_csv_group);
    group = groupDao.getGroup(ext_csv_group.getName());
    assertTrue(group.getName().equals(ext_csv_group.getName()));
    assertTrue(group.getGroupId() == (ext_csv_group.getGroupId()));

    groupDao.deleteAll();
  }

  @Test
  public void testDeleteGroupById() throws Exception {
    GroupRockDao groupDao = new GroupRockDao();
    groupDao.saveGroup(ext_dat_group);
    groupDao.deleteGroup(ext_dat_group.getGroupId());
    Group group = groupDao.getGroup(ext_dat_group.getGroupId());
    assertTrue(group == null);
  }

  @Test
  public void testDeleteGroupByName() throws Exception {
    GroupRockDao groupDao = new GroupRockDao();
    groupDao.saveGroup(ext_dat_group);
    groupDao.deleteGroup(ext_dat_group.getName());
    Group group = groupDao.getGroup(ext_dat_group.getName());
    assertTrue(group == null);
  }
}
