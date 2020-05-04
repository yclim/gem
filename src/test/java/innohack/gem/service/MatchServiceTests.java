package innohack.gem.service;

import com.google.common.collect.Lists;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.match.MatchFileGroup;
import innohack.gem.entity.match.MatchFileRule;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.FilenamePrefix;
import innohack.gem.entity.rule.rules.Rule;
import innohack.gem.web.GEMFileController;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MatchServiceTests {

  @Autowired
  IGEMFileDao gemFileDao;
  @Autowired
  IGroupDao groupDao;
  @Autowired MatchService matchService;
  @Autowired GEMFileController gemFileController;

  Group ext_csv_group;
  Group ext_dat_group;
  Group prefix_d_group;
  GEMFile csvFile = new GEMFile("chats.csv", "src/test/resources");
  GEMFile csvcsvFile = new GEMFile("chats.csv.csv", "src/test/resources");
  GEMFile txtFile = new GEMFile("dump.txt", "src/test/resources");
  GEMFile datFile = new GEMFile("data.dat", "src/test/resources");

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
    for (GEMFile file : gemFileDao.getFiles()) {
      gemFileDao.delete(file.getAbsolutePath());
      matchService.onUpdateEvent(file);
    }
    for (Group group : groupDao.getGroups()) {
      groupDao.deleteGroup(group.getName());
      matchService.onUpdateEvent(group);
    }

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
        for (String f : group.getMatchedFile()) {
          System.out.println(f);
        }
      }
      System.out.println("=====================");
    }
    // ext_csv_group: check matched list
    List<String> matchedFiles = groupDao.getGroup(ext_csv_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(csvFile.getAbsolutePath()));
    assert (matchedFiles.contains(csvcsvFile.getAbsolutePath()));
    assert (matchedFiles.size() == 2);
    // ext_dat_group: check matched list
    matchedFiles = groupDao.getGroup(ext_dat_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(datFile.getAbsolutePath()));
    assert (matchedFiles.size() == 1);
    // prefix_d_group: check matched list
    matchedFiles = groupDao.getGroup(prefix_d_group.getGroupId()).getMatchedFile();
    assert (matchedFiles.contains(datFile.getAbsolutePath()));
    assert (matchedFiles.size() == 1);

    // prefix_d_group: check conflict and file not matched
    matchService.calculateAbnormalMatchCount();
    System.out.println(
        "matchService.getFilesWithoutMatch: " + matchService.getFilesWithoutMatch().size());
    for (GEMFile f : matchService.getFilesWithoutMatch()) {
      System.out.println(f.getAbsolutePath());
    }
    System.out.println(
        "matchService.getFilesWithConflictMatch: "
            + matchService.getFilesWithConflictMatch().size());
    for (GEMFile f : matchService.getFilesWithConflictMatch()) {
      System.out.println(f.getAbsolutePath());
    }
    List<GEMFile> filesWithoutMatch = matchService.getFilesWithoutMatch();
    assert (filesWithoutMatch.contains(txtFile));
    assert (matchService.getFilesWithoutMatch().size() == 1);
    assert (matchService.getFilesWithConflictMatch().contains(datFile));
    assert (matchService.getFilesWithConflictMatch().size() == 1);

    MatchFileRule matchFileRule = matchService.matchFileRuleTable.get(datFile.getAbsolutePath());
    MatchFileGroup matchFileGroup = matchService.matchFileGroupTable.get(datFile.getAbsolutePath());
    assert (matchFileRule.getMatchRuleHashcode().get(prefix_d_group.getRules().get(0).hashCode())
        == true);
    assert (matchFileGroup.getMatchedGroupIds().contains(prefix_d_group.getGroupId()) == true);
    groupDao.deleteGroup(prefix_d_group.getName());
    matchService.onUpdateEvent(prefix_d_group);

    System.out.println("=====================");
    printMatchedCount();

    matchFileRule = matchService.matchFileRuleTable.get(datFile.getAbsolutePath());
    matchFileGroup = matchService.matchFileGroupTable.get(datFile.getAbsolutePath());
    assert (matchFileRule.getMatchRuleHashcode().get(prefix_d_group.getRules().get(0).hashCode())
        == null);
    assert (matchFileGroup.getMatchedGroupIds().contains(prefix_d_group.getGroupId()) == false);

    System.out.println("=====================");
    System.out.println("removed group: " + prefix_d_group.getName());
    printMatchedCount();

    matchFileRule = matchService.matchFileRuleTable.get(txtFile.getAbsolutePath());
    matchFileGroup = matchService.matchFileGroupTable.get(txtFile.getAbsolutePath());
    assert (matchFileRule != null);
    assert (matchFileGroup != null);
    gemFileDao.delete(txtFile.getAbsolutePath());
    matchService.onUpdateEvent(txtFile);
    matchFileRule = matchService.matchFileRuleTable.get(txtFile.getAbsolutePath());
    matchFileGroup = matchService.matchFileGroupTable.get(txtFile.getAbsolutePath());
    assert (matchFileRule == null);
    assert (matchFileGroup == null);

    System.out.println("=====================");
    System.out.println("removed file: " + txtFile.getAbsolutePath());
    printMatchedCount();

    System.out.println("Total groups: " + groupDao.getGroups().size());
    System.out.println("Total files: " + gemFileDao.getFiles().size());

    matchService.matchFileRuleTable.clear();
    matchService.matchFileGroupTable.clear();
    gemFileDao.deleteAll();
    groupDao.deleteAll();
  }

  private void printMatchedCount() {
    for (Group group : groupDao.getGroups()) {
      System.out.println(group.getName() + " matched file count: " + group.getMatchedFile().size());
      for (String path : group.getMatchedFile()) {
        System.out.println(path);
      }
    }
  }
}
