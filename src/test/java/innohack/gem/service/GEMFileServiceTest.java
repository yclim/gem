package innohack.gem.service;

import com.google.common.collect.Lists;
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

  private List<File> getFiles(String path) {
    File dir = new File(path);
    List<File> resultList = Lists.newArrayList();
    File[] fList = dir.listFiles();
    if (fList != null) {
      for (File file : fList) {
        if (file.isFile()) {
          resultList.add(file);
        } else {
          resultList.addAll(getFiles(file.getAbsolutePath()));
        }
      }
    }
    return resultList;
  }

  @Test
  public void testSync() throws Exception {

    // gen file
    String dir = "target/samples/";
    String[] genMockParam = {dir, "5"};
    GenerateMockFiles.main(genMockParam);
    // sync file, test synced file count
    List<GEMFile> list1 = gemFileService.syncFiles("target/samples/");
    assert (getFiles(dir).size() == list1.size());

    // delete 3 files
    int deleteFile = 3;
    for (File f : getFiles(dir)) {
      f.delete();
      deleteFile--;
      if (deleteFile == 0) break;
    }
    // sync file again and test file count again
    List<GEMFile> list = gemFileService.syncFiles("target/samples/");
    assert (list.size() == getFiles(dir).size());
  }
}
