package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import org.junit.jupiter.api.Test;

public class CSVExtractorTest {

  @Test
  public void testExtract() throws Exception {
    GEMFile file = new GEMFile("reviews.csv", "src/test/resources");

    ExtractConfig config = new ExtractConfig();
    CSVExtractor csvExtractor = new CSVExtractor("Id,Review,Time");
    config.setExtractor(csvExtractor);
    config.addColumnNames("Id");
    config.addColumnNames("Review");
    config.addColumnNames("Time");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));

    file.extract();
    ExtractedRecords results = csvExtractor.extract(file, config);
    assertEquals(5, results.getRecords().size());
    assertEquals("Maybe", results.getRecords().get(3).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(3));
  }
}
