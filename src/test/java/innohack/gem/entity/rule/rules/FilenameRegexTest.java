package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class FilenameRegexTest {

  @Test
  public void testCheck() throws Exception {
    FilenameRegex reviewsRegex = new FilenameRegex("review");
    FilenameRegex dataRegex = new FilenameRegex(".*data.*");
    FilenameRegex addressRegex = new FilenameRegex("address");

    GEMFile reviewsFile = new GEMFile("reviews.csv", "src/test/resources");
    GEMFile dataFile = new GEMFile("data.data", "src/test/resources");

    assertTrue(reviewsRegex.check(reviewsFile));
    assertTrue(dataRegex.check(dataFile));

    assertFalse(addressRegex.check(reviewsFile));
    assertFalse(addressRegex.check(dataFile));
  }
}
