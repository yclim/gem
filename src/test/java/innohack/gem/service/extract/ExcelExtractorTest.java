package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.entity.extract.TimestampColumn;
import innohack.gem.core.extract.ExcelExtractor;
import java.io.File;
import org.junit.jupiter.api.Test;

public class ExcelExtractorTest {
  @Test
  public void extractTest() throws Exception {
    File file = new File("src/test/resources/reviews.xlsx");

    ExtractConfig config = new ExtractConfig();
    ExcelExtractor excelExtractor = new ExcelExtractor("reviews", "Id,Review,Time");
    config.setExtractor(excelExtractor);

    config.addColumnNames("Id");
    config.addColumnNames("Message");
    config.addColumnNames("Time");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));
    GEMFile gemFile = GEMMain.extractFeature(file);
    ExtractedRecords results = excelExtractor.extract(gemFile, config);
    assertEquals(5, results.getRecords().size());
    assertEquals("Super Good", results.getRecords().get(2).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(3));
  }
}
