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
import java.util.concurrent.ConcurrentHashMap;
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
    List<GEMFile> files = Lists.newArrayList(csvFile, csvcsvFile, txtFile, datFile);
    List<Group> groups = Lists.newArrayList(ext_csv_group, ext_dat_group, prefix_d_group);

    System.out.println("Total groups: "+groupDao.getGroups().size());
    System.out.println("Total files: "+gemFileDao.getFiles().size());

    for (int i = 0; i < groups.size() + files.size(); i++) {
      if (i % 2 == 0) {
        GEMFile f = files.get(i / 2);
        gemFileDao.saveFile(f);
        matchService.onUpdateEvent(f);
        System.out.println("Added new file: "+f.getAbsolutePath());
      } else {
        Group g = groups.get((i - 1) / 2);
        groupDao.saveGroup(g);
        matchService.onUpdateEvent(g);
        System.out.println("Added new group: "+g.getName());
      }

      for (Group group : groupDao.getGroups()) {
            System.out.println(group.getName()+" matched file count: " + group.getMatchedFile().size());
            for (GEMFile f : group.getMatchedFile()) {
                System.out.println(f.getAbsolutePath());
         }
      }
      System.out.println("=====================");
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
    System.out.println("matchService.getFilesWithoutMatch: "+matchService.getFilesWithoutMatch().size());
    for (GEMFile f : matchService.getFilesWithoutMatch()) {
      System.out.println(f.getAbsolutePath());
    }
    System.out.println("matchService.getFilesWithConflictMatch: "+matchService.getFilesWithConflictMatch().size());
    for (GEMFile f : matchService.getFilesWithConflictMatch()) {
      System.out.println(f.getAbsolutePath());
    }
    assert (matchService.getFilesWithoutMatch().contains(txtFile));
    assert (matchService.getFilesWithoutMatch().size() == 1);
    assert (matchService.getFilesWithConflictMatch().contains(datFile));
    assert (matchService.getFilesWithConflictMatch().size() == 1);

    ConcurrentHashMap<String, HashMap<Rule, Boolean>> mapFileRule =
        MatchService.getMatchedFileRuleTable();
    ConcurrentHashMap<String, Collection<Group>> mapFileGroup =
        MatchService.getMatchedFileGroupTable();

    assert (mapFileRule.get(datFile.getAbsolutePath()).get(prefix_d_group.getRules().get(0))
        == true);
    assert (mapFileGroup.get(datFile.getAbsolutePath()).contains(prefix_d_group) == true);
    groupDao.deleteGroup(prefix_d_group.getName());
    matchService.onUpdateEvent(prefix_d_group);
    assert (mapFileRule.get(datFile.getAbsolutePath()).get(prefix_d_group.getRules().get(0))
        == null);
    assert (mapFileGroup.get(datFile.getAbsolutePath()).contains(prefix_d_group) == false);

    System.out.println("=====================");
    System.out.println("removed group: "+prefix_d_group.getName());
    for (Group group : groupDao.getGroups()) {
      System.out.println(group.getName()+" matched file count: " + group.getMatchedFile().size());
      for (GEMFile f : group.getMatchedFile()) {
        System.out.println(f.getAbsolutePath());
      }
    }
    assert (mapFileRule.get(txtFile.getAbsolutePath()) != null);
    assert (mapFileGroup.get(txtFile.getAbsolutePath()) != null);
    gemFileDao.delete(txtFile.getAbsolutePath());
    matchService.onUpdateEvent(txtFile);
    assert (mapFileRule.get(txtFile.getAbsolutePath()) == null);
    assert (mapFileGroup.get(txtFile.getAbsolutePath()) == null);

    System.out.println("=====================");
    System.out.println("removed file: "+txtFile.getAbsolutePath());
    for (Group group : groupDao.getGroups()) {
      System.out.println(group.getName()+" matched file count: " + group.getMatchedFile().size());
      for (GEMFile f : group.getMatchedFile()) {
        System.out.println(f.getAbsolutePath());
      }
    }

    System.out.println("Total groups: "+groupDao.getGroups().size());
    System.out.println("Total files: "+gemFileDao.getFiles().size());
  }
}
