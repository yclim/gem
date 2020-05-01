package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.util.FileUtilForTesting;
import innohack.gem.filegen.PdfFileGenerator;
import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class TikaContentRegexTest {

  @Test
  public void testValidCheckTikaContent() throws Exception {

    System.out.println("Testing testCheckTikaContent");
    String path = "target/samples/tikaContent/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path, filename));

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(white|black).*");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertTrue(contentRegexCheck.check(gFile));
  }

  @Test
  public void testInvalidCheckTikaContent() throws Exception {

    System.out.println("Testing testCheckTikaContent");
    String path = "target/samples/tikaContent/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path, filename));

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(orange|brown).*");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertFalse(contentRegexCheck.check(gFile));
  }
}
