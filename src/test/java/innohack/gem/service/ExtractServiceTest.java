package innohack.gem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beust.jcommander.internal.Lists;
import innohack.gem.dao.IExtractDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.rule.Group;
import innohack.gem.service.extract.CSVExtractor;
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
  
  private static final String FILENAME = "chats.csv";
  
  @MockBean
  private GroupService groupService;
  
  @Autowired
  private IExtractDao extractDao;

  @Autowired
  private ExtractService extractService;
  
  @BeforeEach
  public void setup() {
    ExtractConfig config = new ExtractConfig();
    config.setExtractor(new CSVExtractor(config));
    config.addColumnNames("Id");
    config.addColumnNames("Sender");
    config.addColumnTimestamp(new TimestampColumn("Time (ms)", "Time", "yyyyMMdd HHmmss"));
    extractDao.saveConfig(GROUP_ID, config);
    
     Group group = new Group();
     group.setMatchedFile(Lists.newArrayList(new GEMFile(FILENAME, "src/test/resources")));
     Mockito.when(groupService.getGroup(Mockito.anyInt())).thenReturn(group);
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
    ExtractedRecords results = extractService.getExtractedRecords(GROUP_ID, FILENAME);
    assertEquals(3, results.getHeaders().size());
    assertEquals("[Id, Sender, Time (ms)]", results.getHeaders().toString());
    
    assertEquals(5, results.size());
    assertEquals("1", results.getRecords().get(0).get(0));
    assertEquals("Sam", results.getRecords().get(0).get(1));
    assertEquals("2020/05/11 16:45:23+0800", results.getRecords().get(0).get(2));
  }
  
}
