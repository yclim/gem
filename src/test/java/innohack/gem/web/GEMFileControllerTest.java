package innohack.gem.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.IMatchFileDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;
import innohack.gem.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GEMFileControllerTest {

  @Autowired IGEMFileDao gemFileDao;
  @Autowired IGroupDao groupDao;
  @Autowired IMatchFileDao matchFileDao;
  @Autowired GEMFileController gemFileController;

  @Test
  public void testSync() throws Exception {
    gemFileDao.deleteAll();
    groupDao.deleteAll();
    matchFileDao.deleteAll();

    gemFileDao.setSyncStatus(1);
    gemFileController.sync("src/test/resources/sync");
    Thread.sleep(1000);
    while (gemFileDao.getSyncStatus() < 1) {
      Thread.sleep(1000);
    }
    System.out.println(gemFileDao.getFileTypes());
//    for (Group g : groupDao.getGroups()) {
//      System.out.println(g.getName() + ": " + g.getGroupId());
//    }
    for (GEMFile file : gemFileDao.getFiles()) {
      String extension = file.getExtension().toUpperCase();
      String defaultGroupName = extension;
      String defaultRuleName = extension + " " + GroupService.DEFAULT_FILEEXT_RULENAME_PREFIX;

      Group group = groupDao.getGroup(defaultGroupName);
      assertTrue(group != null);
      Rule rule = group.getRules().get(0);
      assertTrue(rule instanceof FileExtension);
      String ruleParamVal = rule.getParams().get(0).getValue();
      assertTrue(rule.getName().equalsIgnoreCase(defaultRuleName));
      assertTrue(ruleParamVal.equalsIgnoreCase(extension));
    }
    System.out.println("end");
    gemFileDao.deleteAll();
    groupDao.deleteAll();
    matchFileDao.deleteAll();
  }
}
