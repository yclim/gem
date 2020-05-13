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
    GEMFile file = new GEMFile();
    file.setDirectory("src/test/resources");
    file.setFileName("chats.xlsx");

    ExtractConfig config = new ExtractConfig();

    config.addSheetNames("chats");
    config.addColumnNames("Id");
    config.addColumnNames("Message");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));

    ExcelExtractor extractor = new ExcelExtractor(config);
    ExtractedRecords results = extractor.extract(file);
    assertEquals(5, results.getRecords().size());
    assertEquals("Yes, you looking for me?", results.getRecords().get(2).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(2));
  }
}
