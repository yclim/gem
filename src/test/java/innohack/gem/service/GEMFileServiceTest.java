package innohack.gem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.dao.IGroupDao;
import innohack.gem.entity.GEMFile;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GEMFileServiceTest {

  @Autowired private IGEMFileDao gemFileDao;
  @Autowired private IGroupDao groupDao;
  @Autowired private GEMFileService gemFileService;

  @Test
  public void testSync() throws Exception {
    // sync file, test synced file count
    List<GEMFile> list1 = gemFileService.syncFiles("src/test/resources/sync");
    assertEquals(5, list1.size());

    // sync file again and test file count again
    List<GEMFile> list = gemFileService.syncFiles("src/test/resources/nonexist");
    assertEquals(0, list.size());
    gemFileDao.deleteAll();
    groupDao.deleteAll();
  }
}
