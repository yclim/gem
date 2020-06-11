package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class FileExtensionTest {

  @Test
  public void testCheck() throws Exception {
    FileExtension csvRule = new FileExtension("csv");
    FileExtension datRule = new FileExtension("dat");

    GEMFile csvFile = new GEMFile("reviews.csv", "src/test/resources");
    GEMFile csvDblFile = new GEMFile("reviews.csv.csv", "src/test/resources");
    GEMFile datFile = new GEMFile("data.dat", "src/test/resources");

    assertTrue(csvRule.check(csvFile));
    assertTrue(csvRule.check(csvDblFile));
    assertTrue(datRule.check(datFile));

    assertFalse(datRule.check(csvFile));
    assertFalse(csvRule.check(datFile));
  }
}
