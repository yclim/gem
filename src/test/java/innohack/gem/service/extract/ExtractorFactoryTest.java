package innohack.gem.service.extract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ExtractorFactoryTest {

  @Test
  public void testCreateAllInstance() throws Exception {
    List<AbstractExtractor> extractorList = ExtractorFactory.createAllInstance();
    assertEquals(extractorList.size(), 3, "expect 3 extractors");
    assertEquals(
        extractorList.stream().filter(e -> e instanceof CSVExtractor).count(),
        1,
        "contains CSV Extractor");
    assertEquals(
        extractorList.stream().filter(e -> e instanceof ExcelExtractor).count(),
        1,
        "contains Excel Extractor");
    assertEquals(
        extractorList.stream().filter(e -> e instanceof TikaContentExtractor).count(),
        1,
        "contains Tika Content Extractor");
  }
}
