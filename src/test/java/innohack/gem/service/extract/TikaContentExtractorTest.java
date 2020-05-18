package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.rule.rules.TikaContentRegex;
import innohack.gem.example.TikaExtractor;
import org.junit.jupiter.api.Test;

public class TikaContentExtractorTest {

  @Test
  public void extractTest() throws Exception {
    GEMFile pdfFile =
        new GEMFile(
            "story_0.pdf", "src/test/resources/innohack/gem/entity/rule/rules/TikaMetadataValue");

    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(white|black)");

    // what should we set for the config here for tika content? as there is no row or header in
    // tika extraction
    config.setExtractor(tikaContentExtractor);

    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);

    assertEquals(2, results.getRecords().size());
    assertEquals("WHITE", results.getRecords().get(0).get(0));
    assertEquals("Black", results.getRecords().get(0).get(1));
  }
}
