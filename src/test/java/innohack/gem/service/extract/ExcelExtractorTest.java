package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import org.junit.jupiter.api.Test;

public class ExcelExtractorTest {
  @Test
  public void extractTest() throws Exception {
    GEMFile file = new GEMFile("chats.xlsx", "src/test/resources");

    ExtractConfig config = new ExtractConfig();
    ExcelExtractor excelExtractor = new ExcelExtractor("chats", "Id,Message,Time");
    config.setExtractor(excelExtractor);
    config.addSheetNames("chats");
    config.addColumnNames("Id");
    config.addColumnNames("Message");
    config.addColumnNames("Time");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));

    ExtractedRecords results = excelExtractor.extract(file, config);
    assertEquals(5, results.getRecords().size());
    assertEquals("Yes, you looking for me?", results.getRecords().get(2).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(3));
  }
}
