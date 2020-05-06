package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class TikaMetadataRegexTest {

  @Test
  public void testValidCheckTikaMetadata() throws Exception {

    System.out.println("Testing testValidCheckTikaMetadata");
    String path = "src/test/resources";

    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    TikaMetaValue metadaCheck = new TikaMetaValue("Content-Type", "application/pdf");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertTrue(metadaCheck.check(gFile));
  }

  @Test
  public void testInvalidCheckTikaMetadata() throws Exception {

    System.out.println("Testing testInvalidCheckTikaMetadata");
    String path = "src/test/resources";
    String filenamePrefix = "story_";
    String filename = filenamePrefix + 0 + ".pdf";

    TikaMetaValue metadaCheck = new TikaMetaValue("Owner", "orhorh");

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertFalse(metadaCheck.check(gFile));
  }
}
