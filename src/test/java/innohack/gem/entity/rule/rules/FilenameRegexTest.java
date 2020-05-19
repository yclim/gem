package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class FilenameRegexTest {

  @Test
  public void testCheck() throws Exception {
    FilenameRegex chatsRegex = new FilenameRegex("chat");
    FilenameRegex dataRegex = new FilenameRegex(".*data.*");
    FilenameRegex addressRegex = new FilenameRegex("address");

    GEMFile chatsFile = new GEMFile("chats.csv", "src/test/resources");
    GEMFile dataFile = new GEMFile("data.data", "src/test/resources");

    assertTrue(chatsRegex.check(chatsFile));
    assertTrue(dataRegex.check(dataFile));

    assertFalse(addressRegex.check(chatsFile));
    assertFalse(addressRegex.check(dataFile));
  }
}
