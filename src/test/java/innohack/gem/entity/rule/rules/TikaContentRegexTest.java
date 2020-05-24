package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class TikaContentRegexTest {

  @Test
  public void testValidCheckTikaContent() throws Exception {

    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");
    pdfFile.extract();
    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(white|black).*");
    assertTrue(contentRegexCheck.check(pdfFile));
  }

  @Test
  public void testValidCheckWithoutCaptureGroup() throws Exception {

    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");
    pdfFile.extract();
    TikaContentRegex contentRegexCheck = new TikaContentRegex("(?:white|black)");
    assertTrue(contentRegexCheck.check(pdfFile));

    TikaContentRegex contentRegexCheck2 = new TikaContentRegex("white");
    assertTrue(contentRegexCheck2.check(pdfFile));
  }

  @Test
  public void testInvalidCheckTikaContent() throws Exception {

    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");
    pdfFile.extract();
    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(orange|brown).*");
    pdfFile.extract();
    assertFalse(contentRegexCheck.check(pdfFile));
  }
}
