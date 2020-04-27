package innohack.gem.entity.rule;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.rule.rules.FileExtension;
import innohack.gem.entity.rule.rules.FilenamePrefix;
import innohack.gem.entity.rule.rules.Rule;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class GroupTest {

  @Test
  public void testRuleComparable() throws Exception {
    Rule rule1 = new FileExtension("csv");
    Rule rule2 = new FileExtension("csv");
    Rule rule3 = new FilenamePrefix("csv");
    Rule rule4 = new FilenamePrefix("chat");
    assertTrue(rule1.equals(rule2));
    assertTrue(!rule1.equals(rule3));
    assertTrue(!rule1.equals(rule4));
    assertTrue(!rule3.equals(rule4));

    HashMap<Rule, Boolean> map = new HashMap<Rule, Boolean>();
    map.put(rule1, false);
    assertTrue(map.get(rule1) == false);
    assertTrue(map.get(rule2) == false);
    assertTrue(map.get(rule3) == null);
    assertTrue(map.get(rule4) == null);
  }
}
