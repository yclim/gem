package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.extractor.TimestampColumn;
import java.io.File;
import org.junit.jupiter.api.Test;

public class CSVExtractorTest {

  @Test
  public void testExtract() throws Exception {
    File file = new File("src/test/resources/reviews.csv");

    ExtractConfig config = new ExtractConfig();
    CSVExtractor csvExtractor = new CSVExtractor("Id,Review,Time");
    config.setExtractor(csvExtractor);
    config.addColumnNames("Id");
    config.addColumnNames("Review");
    config.addColumnNames("Time");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));

    GEMFile gemFile = GEMMain.extractFeature(file);
    ExtractedRecords results = csvExtractor.extract(gemFile, config);
    assertEquals(5, results.getRecords().size());
    assertEquals("Maybe", results.getRecords().get(3).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(3));
  }
}
