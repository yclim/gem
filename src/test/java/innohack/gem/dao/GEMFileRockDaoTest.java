package innohack.gem.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.database.RocksDatabase;
import innohack.gem.entity.GEMFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GEMFileRockDaoTest {

  private String filename1 = "dump.txt";
  private String filename2 = "data.dat";
  private String filename3 = "cars_0.xls";
  private String dir = "src/test/resources";
  private String dbName = "TESTDB";
  private RocksDatabase<String, GEMFile> db = RocksDatabase.getInstance(dbName, String.class, GEMFile.class);

  @Test
  void testSaveFile() throws Exception {
    GEMFile f = new GEMFile(filename1, dir);
    db.put(f.getAbsolutePath(), f);
    GEMFile file = db.get(f.getAbsolutePath());
    assertTrue(file.equals(f));
    assertTrue(file.getData() == null);
    db.deleteAll();
  }

  @Test
  void testSaveFileWithExtractedData() throws Exception {
    GEMFile f = new GEMFile(filename1, dir);
    f.extract();
    db.put(f.getAbsolutePath(), f);
    GEMFile file = db.get(f.getAbsolutePath());
    assertTrue(file.equals(f));
    assertTrue(file.getData() != null);
    db.deleteAll();
  }

  @Test
  void testDeleteFile() throws Exception {
    GEMFile f = new GEMFile(filename1, dir);
    db.put(f.getAbsolutePath(), f);
    assertTrue(db.delete(f.getAbsolutePath()));
    assertTrue(db.get(f.getAbsolutePath()) == null);
    db.deleteAll();
  }

  @Test
  void testDeleteAll() throws Exception {
    GEMFile f1 = new GEMFile(filename1, dir);
    GEMFile f2 = new GEMFile(filename2, dir);
    GEMFile f3 = new GEMFile(filename3, dir);
    db.put(f1.getAbsolutePath(), f1);
    db.put(f2.getAbsolutePath(), f2);
    db.put(f3.getAbsolutePath(), f3);
    db.deleteAll();
    List<String> keys = db.scanPrefixKey(f1.getAbsolutePath().substring(0, 5));
    assertTrue(!keys.contains(f1.getAbsolutePath()));
    assertTrue(!keys.contains(f2.getAbsolutePath()));
    assertTrue(!keys.contains(f3.getAbsolutePath()));
    assertTrue(db.getKeys().size() == 0);
  }

  @Test
  void testGetKeyPrefix() throws Exception {
    db.deleteAll();
    GEMFile f1 = new GEMFile(filename1, dir);
    GEMFile f2 = new GEMFile(filename2, dir);
    // db.deleteAll();
    db.put(f1.getAbsolutePath(), f1);
    db.put(f2.getAbsolutePath(), f2);
    List<String> keys = db.scanPrefixKey(f1.getAbsolutePath().substring(0, 5));
    assertTrue(keys.contains(f1.getAbsolutePath()));
    assertTrue(keys.contains(f2.getAbsolutePath()));
    db.deleteAll();
  }

  @Test
  void testGetKeys() throws Exception {
    db.deleteAll();
    GEMFile f1 = new GEMFile(filename1, dir);
    GEMFile f2 = new GEMFile(filename2, dir);
    db.put(f1.getAbsolutePath(), f1);
    db.put(f2.getAbsolutePath(), f2);
    List<String> keys = db.getKeys();
    assertTrue(keys.contains(f1.getAbsolutePath()));
    assertTrue(keys.contains(f2.getAbsolutePath()));
    db.deleteAll();
  }

  @Test
  void testGetAllKeyValue() throws Exception {
    db.deleteAll();
    GEMFile f1 = new GEMFile(filename1, dir);
    GEMFile f2 = new GEMFile(filename2, dir);
    GEMFile f3 = new GEMFile(filename3, dir);
    db.put(f1.getAbsolutePath(), f1);
    db.put(f2.getAbsolutePath(), f2);
    db.put(f3.getAbsolutePath(), f3);
    Map<String, GEMFile> keyValyeMap = db.getKeyValues();
    assertTrue(keyValyeMap.get(f1.getAbsolutePath()).equals(f1));
    assertTrue(keyValyeMap.get(f2.getAbsolutePath()).equals(f2));
    assertTrue(keyValyeMap.get(f3.getAbsolutePath()).equals(f3));
    db.deleteAll();
  }

  @Test
  void testIterator() throws Exception {
    db.deleteAll();

    GEMFile[] flist = {
      new GEMFile(filename1, dir), new GEMFile(filename2, dir), new GEMFile(filename3, dir)
    };
    for (GEMFile f : flist) {
      db.put(f.getAbsolutePath(), f);
    }
    int count = 0;
    RocksDatabase<String, GEMFile>.Iterator<String, GEMFile> itr = db.iterator();
    List<String> keys = new ArrayList<>();
    while (itr.isValid()) {
//      System.out.println(itr.keyValue().key);
//      System.out.println(itr.keyValue().value);
      keys.add((String) itr.keyValue().key);
      itr.next();
      count++;
    }
    itr.close();
    for (GEMFile f : flist) {
      assertTrue(keys.contains(f.getAbsolutePath()));
    }
    assertTrue(count == flist.length);

    db.deleteAll();
  }
}
