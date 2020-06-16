package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.rules.FilenameRegex;
import org.junit.jupiter.api.Test;

public class FilenameRegexTest {

  @Test
  public void testCheck() throws Exception {
    FilenameRegex chatsRegex = new FilenameRegex("reviews");
    FilenameRegex dataRegex = new FilenameRegex(".*data.*");
    FilenameRegex addressRegex = new FilenameRegex("address");

    GEMFile chatsFile = new GEMFile("reviews.csv", "src/test/resources");
    GEMFile dataFile = new GEMFile("data.dat", "src/test/resources");

    assertTrue(chatsRegex.check(chatsFile));
    assertTrue(dataRegex.check(dataFile));

    assertFalse(addressRegex.check(chatsFile));
    assertFalse(addressRegex.check(dataFile));
  }
}
