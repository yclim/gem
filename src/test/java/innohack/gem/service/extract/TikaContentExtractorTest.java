package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.extract.TikaContentExtractor;
import java.io.File;
import org.junit.jupiter.api.Test;

public class TikaContentExtractorTest {

  @Test
  public void testExtractNoGroup() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");

    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("\\bwhite\\b|\\bblack\\b");

    // what should we set for the config here for tika content? as there is no row or header in
    // tika extraction
    config.setExtractor(tikaContentExtractor);
    GEMFile pdfFile = GEMMain.extractFeature(f);
    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);

    assertEquals(1, results.getRecords().size());
    assertEquals("WHITE", results.getRecords().get(0).get(0));
  }

  @Test
  public void testExtractEmpty() throws Exception {
    File f = new File("src/test/resources/extract/story_0.pdf");
    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(Nada)");
    config.setExtractor(tikaContentExtractor);
    GEMFile pdfFile = GEMMain.extractFeature(f);
    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);
    assertEquals(0, results.getRecords().size());
  }

  @Test
  public void testExtractTable() throws Exception {
    File f = new File("src/test/resources/extract/reviews.pdf");
    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(.*?),(.*?),(.*?),(.*)");
    config.setExtractor(tikaContentExtractor);
    GEMFile pdfFile = GEMMain.extractFeature(f);
    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);
    assertEquals(6, results.getRecords().size()); // rows
    assertEquals(4, results.getRecords().get(0).size()); // columns
    assertEquals("Sender", results.getRecords().get(0).get(1)); // header
    assertEquals("Good", results.getRecords().get(1).get(2)); // value
    assertEquals("20200511 164635", results.getRecords().get(5).get(3)); // value
  }
}
