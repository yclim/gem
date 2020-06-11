package innohack.gem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beust.jcommander.internal.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedFile;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.extractor.TimestampColumn;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.dao.IExtractDao;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.service.extract.AbstractExtractor;
import innohack.gem.service.extract.CSVExtractor;
import innohack.gem.service.extract.ExcelExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Import({SkipRockDBConfig.class})
@ExtendWith(SpringExtension.class)
public class ExtractServiceTest {

  private static final int GROUP_ID = 1;

  private static final String FILENAME = "reviews.csv";

  private static final GEMFile GEM_FILE = new GEMFile(FILENAME, "src/test/resources");

  @MockBean private GroupService groupService;

  @MockBean private IGEMFileDao gemFileDao;

  @Autowired private IExtractDao extractDao;

  @Autowired private ExtractService extractService;

  @BeforeEach
  public void setup() {
    ExtractConfig config = new ExtractConfig();
    CSVExtractor csvExtractor = new CSVExtractor("Id,Sender,Time");
    config.setExtractor(csvExtractor);
    config.addColumnNames("Identifier");
    config.addColumnNames("Name");
    config.addColumnNames("RawTime");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "RawTime", "yyyyMMdd HHmmss"));
    extractDao.saveConfig(GROUP_ID, config);

    Group group = new Group();

    GEMFile gemFile = GEM_FILE.extract();
    group.setMatchedFile(Lists.newArrayList(gemFile));

    Mockito.when(groupService.getGroup(Mockito.anyInt())).thenReturn(group);
    Mockito.when(gemFileDao.getFile(Mockito.anyString(), Mockito.anyString())).thenReturn(gemFile);
  }

  @Test
  public void testExtract() throws Exception {
    List<ExtractedFile> files = extractService.extract(GROUP_ID);
    assertEquals(1, files.size());
    assertEquals(FILENAME, files.get(0).getFilename());
    assertEquals(5, files.get(0).getCount());
  }

  @Test
  public void testGetExtractedFiles() throws Exception {
    extractService.extract(GROUP_ID);
    List<ExtractedFile> files = extractService.getExtractedFiles(GROUP_ID);
    assertEquals(1, files.size());
    assertEquals(FILENAME, files.get(0).getFilename());
    assertEquals(5, files.get(0).getCount());
  }

  @Test
  public void testGetExtractedRecords() throws Exception {
    extractService.extract(GROUP_ID);
    ExtractedRecords results =
        extractService.getExtractedRecords(GROUP_ID, GEM_FILE.getAbsolutePath());
    assertEquals(4, results.getHeaders().size());
    assertEquals("[Identifier, Name, RawTime, Time (ms)]", results.getHeaders().toString());

    assertEquals(5, results.size());
    assertEquals("1", results.getRecords().get(0).get(0));
    assertEquals("Sam", results.getRecords().get(0).get(1));
    assertEquals("20200511 164523", results.getRecords().get(0).get(2));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(3));
  }

  @Test
  public void testGetExtractorTemplates() throws Exception {
    List<AbstractExtractor> extractorList = extractService.getExtractorTemplates();
    assertEquals(extractorList.size(), 3, "expect 3 extractors");
  }

  @Test
  void testUpdateExtractConfig() {
    int csvGroup = 1;
    CSVExtractor csvExtractor = new CSVExtractor("a,b,c");
    ExtractConfig csvConfig = new ExtractConfig();
    csvConfig.setGroupId(csvGroup);
    csvConfig.setExtractor(csvExtractor);

    int excelGroup = 2;
    ExcelExtractor excelExtractor = new ExcelExtractor("sheet1", "x,y,z");
    ExtractConfig excelConfig = new ExtractConfig();
    excelConfig.setGroupId(excelGroup);
    excelConfig.setExtractor(excelExtractor);

    extractService.updateExtractConfig(csvGroup, csvConfig);
    extractService.updateExtractConfig(excelGroup, excelConfig);

    assertTrue(
        extractService.getExtractConfig(csvGroup).getExtractor() instanceof CSVExtractor,
        "csv extractor saved");
    assertEquals(
        extractService.getExtractConfig(csvGroup).getExtractor().getParams().get(0).getValue(),
        "a,b,c",
        "csv extractor's param saved");
    assertTrue(
        extractService.getExtractConfig(excelGroup).getExtractor() instanceof ExcelExtractor,
        "excel extractor saved");
    assertEquals(
        extractService.getExtractConfig(excelGroup).getExtractor().getParams().get(0).getValue(),
        "sheet1",
        "excel extractor's param1 saved");
    assertEquals(
        extractService.getExtractConfig(excelGroup).getExtractor().getParams().get(1).getValue(),
        "x,y,z",
        "excel extractor's param2 saved");
  }
}
