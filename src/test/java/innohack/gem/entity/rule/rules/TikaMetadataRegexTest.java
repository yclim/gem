package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.util.FileUtilForTesting;
import innohack.gem.filegen.PdfFileGenerator;
import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class TikaMetadataRegexTest {

  @Test
  public void testValidCheckTikaMetadata() throws Exception {

    System.out.println("Testing testValidCheckTikaMetadata");
    String path = "target/samples/tikaContent/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaMetaValue metadaCheck = new TikaMetaValue("Content-Type", "application/pdf");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertTrue(metadaCheck.check(gFile));
  }

  @Test
  public void testInvalidCheckTikaMetadata() throws Exception {

    System.out.println("Testing testInvalidCheckTikaMetadata");
    String path = "target/samples/tikaContent/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaMetaValue metadaCheck = new TikaMetaValue("Owner", "orhorh");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertFalse(metadaCheck.check(gFile));
  }
}
