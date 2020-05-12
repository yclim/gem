package innohack.gem.service;

import com.google.common.collect.Lists;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.MatchFileRockDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.match.MatchFileGroup;
import innohack.gem.entity.match.MatchFileRule;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.FilenamePrefix;
import innohack.gem.entity.rule.rules.Rule;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MatchServiceTests {

  @Autowired private IGEMFileDao gemFileDao;
  @Autowired private IGroupDao groupDao;
  @Autowired private MatchService matchService;
  @Autowired private MatchFileRockDao matchFileDao;
  
  private Group ext_csv_group;
  private Group ext_dat_group;
  private Group prefix_d_group;
  private GEMFile csvFile = new GEMFile("chats.csv", "src/test/resources");
  private GEMFile csvcsvFile = new GEMFile("chats.csv.csv", "src/test/resources");
  private GEMFile txtFile = new GEMFile("dump.txt", "src/test/resources");
  private GEMFile datFile = new GEMFile("data.dat", "src/test/resources");

  public MatchServiceTests() {
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
  public void testMatchedListInGroup() throws Exception {
    if (MatchService.getMatchFileRuleTable() != null) {
      MatchService.getMatchFileRuleTable().clear();
    }
    if (MatchService.getMatchFileGroupTable() != null) {
      MatchService.getMatchFileGroupTable().clear();
    }
    for (GEMFile file : gemFileDao.getFiles()) {
      gemFileDao.delete(file.getAbsolutePath());
      matchService.onUpdateEvent(file);
    }
    for (Group group : groupDao.getGroups()) {
      groupDao.deleteGroup(group.getName());
      matchService.onUpdateEvent(group);
    }
    matchFileDao.deleteAll();

    List<GEMFile> files = Lists.newArrayList(csvFile, csvcsvFile, txtFile, datFile);
    List<Group> groups = Lists.newArrayList(ext_csv_group, ext_dat_group, prefix_d_group);

    System.out.println("Total groups: " + groupDao.getGroups().size());
    System.out.println("Total files: " + gemFileDao.getFiles().size());

    for (int i = 0; i < groups.size() + files.size(); i++) {
      if (i % 2 == 0) {
        GEMFile f = files.get(i / 2);
        gemFileDao.saveFile(f);
        matchService.onUpdateEvent(f);
        System.out.println("Added new file: " + f.getAbsolutePath());
      } else {
        Group g = groups.get((i - 1) / 2);
        groupDao.saveGroup(g);
        matchService.onUpdateEvent(g);
        System.out.println("Added new group: " + g.getName());
      }

      for (Group group : groupDao.getGroups()) {
        System.out.println(
            group.getName() + " matched file count: " + group.getMatchedFile().size());
        for (GEMFile f : group.getMatchedFile()) {
          System.out.println(f.getAbsolutePath());
        }
      }
      System.out.println("=====================");
    }
    // ext_csv_group: check matched list
    List<GEMFile> matchedFiles = groupDao.getGroup(ext_csv_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(new GEMFile(csvFile.getAbsolutePath())));
    assert (matchedFiles.contains(new GEMFile(csvcsvFile.getAbsolutePath())));
    assert (matchedFiles.size() == 2);
    // ext_dat_group: check matched list
    matchedFiles = groupDao.getGroup(ext_dat_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(new GEMFile(datFile.getAbsolutePath())));
    assert (matchedFiles.size() == 1);
    // prefix_d_group: check matched list
    matchedFiles = groupDao.getGroup(prefix_d_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(new GEMFile(datFile.getAbsolutePath())));
    assert (matchedFiles.size() == 1);

    boolean exist = false;
    // prefix_d_group: check conflict and file not matched
    Map<String, List<MatchFileGroup>> countResult = matchService.getMatchCount();
    System.out.println("matchService.getFilesWithoutMatch: ");
    for (MatchFileGroup mfg : countResult.get(MatchService.NO_MATCH_TAG)) {
      System.out.println(mfg.getFilePath());
      if (mfg.getFilePath().equals(txtFile.getAbsolutePath())) {
        exist = true;
        break;
      }
    }
    assert (matchService.getFilesWithoutMatch().size() == 1);
    assert (exist);

    exist = false;
    System.out.println("matchService.getFilesWithConflictMatch: ");
    for (MatchFileGroup mfg : countResult.get(MatchService.CONFLICT_TAG)) {
      System.out.println(mfg.getFilePath());
      if (mfg.getFilePath().equals(datFile.getAbsolutePath())) {
        exist = true;
        break;
      }
    }

    assert (exist);
    assert (matchService.getFilesWithConflictMatch().size() == 1);

    MatchFileRule matchFileRule =
        MatchService.getMatchFileRuleTable().get(datFile.getAbsolutePath());
    MatchFileGroup matchFileGroup =
        MatchService.getMatchFileGroupTable().get(datFile.getAbsolutePath());
    assert (matchFileRule.getMatchRuleHashcode().get(prefix_d_group.getRules().get(0).hashCode())
        == true);
    assert (matchFileGroup.getMatchedGroupIds().contains(prefix_d_group.getGroupId()) == true);
    groupDao.deleteGroup(prefix_d_group.getName());
    matchService.onUpdateEvent(prefix_d_group);

    System.out.println("=====================");
    printMatchedCount();

    matchFileRule = MatchService.getMatchFileRuleTable().get(datFile.getAbsolutePath());
    matchFileGroup = MatchService.getMatchFileGroupTable().get(datFile.getAbsolutePath());
    assert (matchFileRule.getMatchRuleHashcode().get(prefix_d_group.getRules().get(0).hashCode())
        == null);
    assert (matchFileGroup.getMatchedGroupIds().contains(prefix_d_group.getGroupId()) == false);

    System.out.println("=====================");
    System.out.println("removed group: " + prefix_d_group.getName());
    printMatchedCount();

    matchFileRule = MatchService.getMatchFileRuleTable().get(txtFile.getAbsolutePath());
    matchFileGroup = MatchService.getMatchFileGroupTable().get(txtFile.getAbsolutePath());
    assert (matchFileRule != null);
    assert (matchFileGroup != null);
    gemFileDao.delete(txtFile.getAbsolutePath());
    matchService.onUpdateEvent(txtFile);
    matchFileRule = MatchService.getMatchFileRuleTable().get(txtFile.getAbsolutePath());
    matchFileGroup = MatchService.getMatchFileGroupTable().get(txtFile.getAbsolutePath());
    assert (matchFileRule == null);
    assert (matchFileGroup == null);

    System.out.println("=====================");
    System.out.println("removed file: " + txtFile.getAbsolutePath());
    printMatchedCount();

    System.out.println("Total groups: " + groupDao.getGroups().size());
    System.out.println("Total files: " + gemFileDao.getFiles().size());

    MatchService.getMatchFileRuleTable().clear();
    MatchService.getMatchFileGroupTable().clear();
    gemFileDao.deleteAll();
    groupDao.deleteAll();
    matchFileDao.deleteAll();
  }

  private void printMatchedCount() {
    for (Group group : groupDao.getGroups()) {
      System.out.println(group.getName() + " matched file count: " + group.getMatchedFile().size());
      for (GEMFile file : group.getMatchedFile()) {
        System.out.println(file.getAbsolutePath());
      }
    }
  }
}
