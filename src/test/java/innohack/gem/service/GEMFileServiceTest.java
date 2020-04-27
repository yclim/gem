package innohack.gem.service;

import innohack.gem.dao.GEMFileDao;
import innohack.gem.dao.GroupDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.Group;
import innohack.gem.filegen.GenerateMockFiles;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GEMFileServiceTest {

  @Autowired GEMFileDao gemFileDao;
  @Autowired GroupDao groupDao;
  @Autowired GEMFileService gemFileService;

  Group csv_group;
  Group data_group;
  GEMFile csvFile = new GEMFile("chats.csv", "src/test/resources");
  GEMFile txtFile = new GEMFile("dump.txt", "src/test/resources");
  GEMFile datFile = new GEMFile("data.dat", "src/test/resources");

  @Test
  public void testSync() throws Exception {

    // gen file
    String[] genMockParam = {"target/samples/", "5"};
    GenerateMockFiles.main(genMockParam);
    File folder = new File("target/samples/");
    // sync file, test synced file count
    List<GEMFile> list1 = gemFileService.syncFiles("target/samples/");
    assert (folder.listFiles().length == list1.size());

    // delete 3 files
    int deleteFile = 3;
    for (File f : folder.listFiles()) {
      f.delete();
      deleteFile--;
      if (deleteFile == 0) break;
    }
    // sync file again and test file count again
    List<GEMFile> list = gemFileService.syncFiles("target/samples/");
    assert (list.size() == new File("target/samples/").listFiles().length);
  }
}
