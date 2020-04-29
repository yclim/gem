package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.util.FileUtilForTesting;
import innohack.gem.example.tika.TikaUtil;
import innohack.gem.example.util.FileUtil;
import innohack.gem.filegen.PdfFileGenerator;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(white|black).*");

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      assertTrue(contentRegexCheck.check(gFile));
    }
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

    PdfFileGenerator.generateFixedTextPdfFiles(1, Paths.get(path.toString(), filename));

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    TikaContentRegex contentRegexCheck = new TikaContentRegex(".*(orange|brown).*");

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      assertFalse(contentRegexCheck.check(gFile));
    }
  }
}
