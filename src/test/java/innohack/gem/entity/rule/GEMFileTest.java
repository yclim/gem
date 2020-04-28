package innohack.gem.entity.rule;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GEMFileTest {

  @Test
  public void testFileComparable() throws Exception {
    GEMFile csvFile1 = new GEMFile("chats.csv", "src/test/resources");
    GEMFile csvFile2 = new GEMFile("chats.csv", "src/test/resources");
    GEMFile datFile = new GEMFile("data.dat", "src/test/resources");
    List<GEMFile> list = new ArrayList<GEMFile>();
    list.add(csvFile2);
    assertTrue(list.contains(csvFile2));
    assertTrue(!list.contains(datFile));
    assertTrue(csvFile1.equals(csvFile2));
    assertTrue(!csvFile1.equals(datFile));
  }
}
