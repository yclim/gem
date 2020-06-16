package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import java.io.File;
import org.junit.jupiter.api.Test;

public class TikaMetadataValueTest {

  @Test
  public void testValidCheckTikaMetadata() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");
    GEMFile gemFile = GEMMain.extractFeature(f);
    TikaMetaValue metadaCheck = new TikaMetaValue("Content-Type", "application/pdf");
    assertTrue(metadaCheck.check(gemFile));
  }

  @Test
  public void testInvalidCheckTikaMetadata() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");
    GEMFile gemFile = GEMMain.extractFeature(f);
    TikaMetaValue metadaCheck = new TikaMetaValue("Owner", "orhorh");
    assertFalse(metadaCheck.check(gemFile));
  }
}
