package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class TikaMetadataValueTest {

  @Test
  public void testValidCheckTikaMetadata() throws Exception {
    GEMFile pdfFile =
        new GEMFile("story_0.pdf", "src/test/resources/innohack/gem/entity/rule/rules");
    pdfFile.extract();
    TikaMetaValue metadaCheck = new TikaMetaValue("Content-Type", "application/pdf");
    assertTrue(metadaCheck.check(pdfFile));
  }

  @Test
  public void testInvalidCheckTikaMetadata() throws Exception {
    GEMFile pdfFile =
        new GEMFile("story_0.pdf", "src/test/resources/innohack/gem/entity/rule/rules");
    pdfFile.extract();
    TikaMetaValue metadaCheck = new TikaMetaValue("Owner", "orhorh");
    assertFalse(metadaCheck.check(pdfFile));
  }
}
