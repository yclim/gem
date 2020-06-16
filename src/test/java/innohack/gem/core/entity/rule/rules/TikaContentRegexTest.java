package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.rules.TikaContentRegex;
import java.io.File;
import org.junit.jupiter.api.Test;

public class TikaContentRegexTest {

  @Test
  public void testValidCheckTikaContent() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");
    GEMFile gemFile = GEMMain.extractFeature(f);
    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(white|black).*");
    assertTrue(contentRegexCheck.check(gemFile));
  }

  @Test
  public void testValidCheckWithoutCaptureGroup() throws Exception {

    File f = new File("src/test/resources/extract/story_0.pdf");
    GEMFile gemFile = GEMMain.extractFeature(f);
    TikaContentRegex contentRegexCheck = new TikaContentRegex("(?:white|black)");
    assertTrue(contentRegexCheck.check(gemFile));

    TikaContentRegex contentRegexCheck2 = new TikaContentRegex("white");
    assertTrue(contentRegexCheck2.check(gemFile));
  }

  @Test
  public void testInvalidCheckTikaContent() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");
    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(orange|brown).*");
    GEMFile gemFile = GEMMain.extractFeature(f);
    assertFalse(contentRegexCheck.check(gemFile));
  }
}
