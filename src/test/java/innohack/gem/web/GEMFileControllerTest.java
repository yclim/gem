package innohack.gem.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.dao.GEMFileDao;
import innohack.gem.dao.GroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GEMFileControllerTest {

  @Autowired GEMFileDao gemFileDao;
  @Autowired GroupDao groupDao;
  @Autowired GEMFileController gemFileController;

  @Test
  public void testSync() throws Exception {
    gemFileController.sync("src/test/resources");
    int counter = 1;
    for (GEMFile file : gemFileDao.getFiles()) {
      String extension = file.getExtension().toUpperCase();
      String defaultGroupName = extension;
      String defaultRuleName = "FE-" + counter++;

      Group group = groupDao.getGroup(defaultGroupName);
      assertTrue(group != null);
      Rule rule = group.getRules().get(0);
      assertTrue(rule instanceof FileExtension);
      String ruleParamVal = rule.getParams().get(0).getValue();
      assertTrue(rule.getName().equalsIgnoreCase(defaultRuleName));
      assertTrue(ruleParamVal.equalsIgnoreCase(extension));
    }
  }
}
