package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import org.junit.jupiter.api.Test;

public class TikaContentExtractorTest {

  @Test
  public void testExtract() throws Exception {
    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");

    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(white|black)");

    // what should we set for the config here for tika content? as there is no row or header in
    // tika extraction
    config.setExtractor(tikaContentExtractor);

    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);

    assertEquals(2, results.getRecords().size());
    assertEquals("WHITE", results.getRecords().get(0).get(0));
    assertEquals("Black", results.getRecords().get(1).get(0));
  }
  
  @Test
  public void testExtractNoGroup() throws Exception {
    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");

    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("\\bwhite\\b|\\bblack\\b");

    // what should we set for the config here for tika content? as there is no row or header in
    // tika extraction
    config.setExtractor(tikaContentExtractor);

    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);

    assertEquals(1, results.getRecords().size());
    assertEquals("WHITE", results.getRecords().get(0).get(0));
  }

  @Test
  public void testExtractEmpty() throws Exception {
    GEMFile pdfFile = new GEMFile("story_0.pdf", "src/test/resources/extract");
    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(Nada)");
    config.setExtractor(tikaContentExtractor);

    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);
    assertEquals(0, results.getRecords().size());
  }

  @Test
  public void testExtractTable() throws Exception {
    GEMFile pdfFile = new GEMFile("reviews.pdf", "src/test/resources/extract");
    ExtractConfig config = new ExtractConfig();
    TikaContentExtractor tikaContentExtractor = new TikaContentExtractor("(.*?),(.*?),(.*?),(.*)");
    config.setExtractor(tikaContentExtractor);

    ExtractedRecords results = tikaContentExtractor.extract(pdfFile, config);
    assertEquals(6, results.getRecords().size()); // rows
    assertEquals(4, results.getRecords().get(0).size()); // columns
    assertEquals("Sender", results.getRecords().get(0).get(1)); // header
    assertEquals("Good", results.getRecords().get(1).get(2)); // value
    assertEquals("20200511 164635", results.getRecords().get(5).get(3)); // value
  }
}
