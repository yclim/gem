package innohack.gem.service;

import com.google.common.collect.Lists;
import innohack.gem.dao.GEMFileDao;
import innohack.gem.dao.GroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.FilenamePrefix;
import innohack.gem.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MatchServiceTests {

  @Autowired GEMFileDao gemFileDao;
  @Autowired GroupDao groupDao;
  @Autowired MatchService matchService;

  Group ext_csv_group;
  Group ext_dat_group;
  Group prefix_d_group;
  GEMFile csvFile = new GEMFile("chats.csv", "src/test/resources");
  GEMFile csvcsvFile = new GEMFile("chats.csv.csv", "src/test/resources");
  GEMFile txtFile = new GEMFile("dump.txt", "src/test/resources");
  GEMFile datFile = new GEMFile("data.dat", "src/test/resources");

  public MatchServiceTests() {
    this.ext_csv_group = new Group();
    ext_csv_group.setName("csv_group");
    Rule rule1 = new FileExtension("csv");
    Rule rule2 = new FilenamePrefix("chats");
    List<Rule> csv_group_rules = new ArrayList<Rule>();
    csv_group_rules.add(rule1);
    csv_group_rules.add(rule2);
    ext_csv_group.setRules(csv_group_rules);
    ext_dat_group = new Group();
    ext_dat_group.setName("data_group");
    Rule rule3 = new FileExtension("dat");
    List<Rule> dat_group_rules2 = new ArrayList<Rule>();
    dat_group_rules2.add(rule3);
    ext_dat_group.setRules(dat_group_rules2);
    prefix_d_group = new Group();
    prefix_d_group.setName("prefix_da_group");
    Rule rule4 = new FilenamePrefix("da");
    List<Rule> prefix_d_group_rules = new ArrayList<Rule>();
    prefix_d_group_rules.add(rule4);
    prefix_d_group.setRules(prefix_d_group_rules);
  }

  @Test
  public void testMatchedListInGroup() throws Exception {
    List<GEMFile> files = Lists.newArrayList(csvFile, csvcsvFile, txtFile, datFile);
    List<Group> groups = Lists.newArrayList(ext_csv_group, ext_dat_group, prefix_d_group);

    for (int i = 0; i < groups.size() + files.size(); i++) {
      if (i % 2 == 0) {
        GEMFile f = files.get(i / 2);
        gemFileDao.saveFile(f);
        matchService.onUpdateFile(f);
      } else {
        Group g = groups.get((i - 1) / 2);
        groupDao.saveGroup(g);
        matchService.onUpdateGroupRule(g);
      }
    }

    // ext_csv_group: check matched list
    assert (ext_csv_group.getMatchedFile().contains(csvFile));
    assert (ext_csv_group.getMatchedFile().contains(csvcsvFile));
    assert (ext_csv_group.getMatchedFile().size() == 2);
    // ext_dat_group: check matched list
    assert (ext_dat_group.getMatchedFile().contains(datFile));
    assert (ext_dat_group.getMatchedFile().size() == 1);
    // prefix_d_group: check matched list
    assert (prefix_d_group.getMatchedFile().contains(datFile));
    assert (prefix_d_group.getMatchedFile().size() == 1);

    // prefix_d_group: check conflict and file not matched
    matchService.calculateAbnormalMatchCount();
    assert (matchService.getFilesWithoutMatch().contains(txtFile));
    assert (matchService.getFilesWithoutMatch().size() == 1);
    assert (matchService.getFilesWithConflictMatch().contains(datFile));
    assert (matchService.getFilesWithConflictMatch().size() == 1);

    HashMap<String, HashMap<Rule, Boolean>> mapFileRule = MatchService.getMatchedFileRuleTable();
    HashMap<String, Collection<Group>> mapFileGroup = MatchService.getMatchedFileGroupTable();

    assert (mapFileRule.get(datFile.getAbsolutePath()).get(prefix_d_group.getRules().get(0))
        == true);
    assert (mapFileGroup.get(datFile.getAbsolutePath()).contains(prefix_d_group) == true);
    groupDao.deleteGroup(prefix_d_group.getName());
    matchService.onUpdateGroupRule(prefix_d_group);
    assert (mapFileRule.get(datFile.getAbsolutePath()).get(prefix_d_group.getRules().get(0))
        == null);
    assert (mapFileGroup.get(datFile.getAbsolutePath()).contains(prefix_d_group) == false);

    assert (mapFileRule.get(txtFile.getAbsolutePath()) != null);
    assert (mapFileGroup.get(txtFile.getAbsolutePath()) != null);
    gemFileDao.delete(txtFile.getAbsolutePath());
    matchService.onUpdateFile(txtFile);
    assert (mapFileRule.get(txtFile.getAbsolutePath()) == null);
    assert (mapFileGroup.get(txtFile.getAbsolutePath()) == null);
  }

  @Test
  public void testCheckMatching() throws Exception {
    assert (matchService.checkMatching(ext_csv_group, csvFile));
    assert (!matchService.checkMatching(ext_csv_group, txtFile));
    assert (matchService.checkMatching(ext_dat_group, datFile));
    assert (!matchService.checkMatching(ext_csv_group, datFile));

    // check cache matchedFileRuleTable
    assert (MatchService.getMatchedFileRuleTable()
        .get(csvFile.getAbsolutePath())
        .get(ext_csv_group.getRules().get(0)));
    assert (!MatchService.getMatchedFileRuleTable()
        .get(txtFile.getAbsolutePath())
        .get(ext_csv_group.getRules().get(0)));
    assert (MatchService.getMatchedFileRuleTable()
        .get(datFile.getAbsolutePath())
        .get(ext_dat_group.getRules().get(0)));
    assert (!MatchService.getMatchedFileRuleTable()
        .get(datFile.getAbsolutePath())
        .get(ext_csv_group.getRules().get(0)));
  }

  /*
  @Test
  public void testNewFileWithNoGroup() throws Exception {
    MatchService matchService = new MatchService();
    gemFileDao.saveFile(csvFile);
    matchService.onNewFile(csvFile);
    assertTrue(MatchService.matchedGroupTable.size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==0);
    gemFileDao.saveFile(txtFile);
    matchService.onNewFile(txtFile);
    assertTrue(MatchService.matchedGroupTable.size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).size() ==0);
    gemFileDao.saveFile(datFile);
    matchService.onNewFile(datFile);
    assertTrue(MatchService.matchedGroupTable.size() ==3);
    assertTrue(MatchService.matchedGroupTable.get(datFile.getAbsolutePath()).size() ==0);
  }

  @Test
  public void testNewGroupWithNoFile() throws Exception {
    GroupController groupController = new GroupController();
    MatchService matchService = new MatchService();
    groupDao.saveGroup(data_group);
    matchService.onNewGroup(data_group);
    assertTrue(MatchService.matchedGroupTable.size() ==0);
  }

  @Test
  public void testNewFileWithExistingGroup() throws Exception {
    MatchService matchService = new MatchService();
    //new group
    groupDao.saveGroup(data_group);
    matchService.onNewGroup(data_group);

    //new file
    gemFileDao.saveFile(csvFile);
    matchService.onNewFile(csvFile);
    assertTrue(MatchService.matchedGroupTable.size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).get(data_group.getName()) ==false);

    //new file
    gemFileDao.saveFile(txtFile);
    matchService.onNewFile(txtFile);
    assertTrue(MatchService.matchedGroupTable.size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).get(data_group.getName()) ==false);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).get(data_group.getName()) ==false);

    //new group
    groupDao.saveGroup(csv_group);
    matchService.onNewGroup(csv_group);
    assertTrue(MatchService.matchedGroupTable.size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).get(csv_group.getName()) ==true);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).get(csv_group.getName()) ==false);


    //new file
    gemFileDao.saveFile(datFile);
    matchService.onNewFile(datFile);
    assertTrue(MatchService.matchedGroupTable.size() ==3);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(datFile.getAbsolutePath()).size() ==2);
    assertTrue(MatchService.matchedGroupTable.get(datFile.getAbsolutePath()).get(csv_group.getName()) ==false);
    assertTrue(MatchService.matchedGroupTable.get(datFile.getAbsolutePath()).get(data_group.getName()) ==true);

    matchService.calculateMatchCount();
    //remove group
    groupDao.deleteGroup(data_group.getName());
    matchService.onRemoveGroup(data_group.getName());
    assertTrue(MatchService.matchedGroupTable.size() ==3);
    assertTrue(MatchService.matchedGroupTable.get(txtFile.getAbsolutePath()).size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(csvFile.getAbsolutePath()).size() ==1);
    assertTrue(MatchService.matchedGroupTable.get(datFile.getAbsolutePath()).size() ==1);

    matchService.calculateMatchCount();

  }*/

}
