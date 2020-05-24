package innohack.gem.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.dao.IMatchFileDao;
import innohack.gem.entity.rule.Group;
import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.Rule;
import innohack.gem.service.GroupService;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// @SpringBootTest //use this for integration test with RockDB
@Import(ControllerConfig.class)
@ExtendWith(SpringExtension.class)
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

    List<String> extensions =
        gemFileDao.getFiles().stream()
            .map(f -> f.getExtension())
            .distinct()
            .collect(Collectors.toList());

    int counter = 1;
    for (String extension : extensions) {
      String defaultGroupName = extension + "-" + "group";
      String defaultRuleName = GroupService.DEFAULT_FILEEXT_RULENAME_PREFIX + "-" + counter;
      Group group = groupDao.getGroup(defaultGroupName);
      assertTrue(group != null);
      Rule rule = group.getRules().get(0);
      assertTrue(rule instanceof FileExtension);
      String ruleParamVal = rule.getParams().get(0).getValue();
      assertTrue(rule.getName().equalsIgnoreCase(defaultRuleName));
      assertTrue(ruleParamVal.equalsIgnoreCase(extension));
      counter++;
    }

    gemFileDao.deleteAll();
    groupDao.deleteAll();
    matchFileDao.deleteAll();
  }
}
