package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class TikaContentRegexTest {

  @Test
  public void testValidCheckTikaContent() throws Exception {

    System.out.println("Testing testCheckTikaContent");
    String path = "src/test/resources";

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(white|black).*");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertTrue(contentRegexCheck.check(gFile));
  }

  @Test
  public void testInvalidCheckTikaContent() throws Exception {

    System.out.println("Testing testCheckTikaContent");
    String path = "src/test/resources";
    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(orange|brown).*");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertFalse(contentRegexCheck.check(gFile));
  }
}
