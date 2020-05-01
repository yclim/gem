package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.database.RocksDatabase;
import innohack.gem.entity.GEMFile;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GEMFileRockDao implements IGEMFileDao {

  static final String DB_NAME = "GEMFile";
  // private static ConcurrentHashMap<String, GEMFile> featureStore = new ConcurrentHashMap<>();
  private static String directoryStore = "";
  RocksDatabase gemFileDb =
      new RocksDatabase<String, GEMFile>(DB_NAME, String.class, GEMFile.class);
  RocksDatabase gemFileFileTypesDb =
      new RocksDatabase<String, Boolean>(DB_NAME + "_FILE_TYPE", String.class, Boolean.class);
  // public static Set<String> fileTypeStore = new HashSet<String>();

  // Get current directory path where files uploaded

  /**
   * Find document in feature store by file name and directory
   *
   * @return directory path
   */
  @Override
  public String getCurrentDirectory() {
    return directoryStore;
  }

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  @Override
  public List<String> getFileTypes() {
    return gemFileFileTypesDb.getKeys();
  }

  @Override
  public void deleteAll() {
    gemFileDb.deleteAll();
    gemFileFileTypesDb.deleteAll();
  }

  @Override
  public void delete(String absolutePath) {
    gemFileDb.delete(absolutePath);
  }

  // This method get file data from feature store
  @Override
  public GEMFile getFile(String filename, String directory) {
    Object o = gemFileDb.get(Paths.get(directory, filename).toString());
    if (o instanceof GEMFile) {
      return (GEMFile) o;
    }
    return null;
  }

  // This method get file data from feature store
  @Override
  public GEMFile getFileByAbsolutePath(String absolutePath) {
    return (GEMFile) gemFileDb.get(absolutePath);
  }

  @Override
  public List<GEMFile> findByName(String filename) {
    List<GEMFile> l = Lists.newArrayList();
    List<GEMFile> list = gemFileDb.getValues();
    for (GEMFile f : list) {
      if (f.getFileName().contains(filename)) {
        l.add(f);
      }
    }
    return l;
  }

  /**
   * Find document metadata by document file extension
   *
   * @param extension document file extension
   * @return list of document metadata
   */
  @Override
  public List<GEMFile> findByExtension(String extension) {
    List<GEMFile> l = Lists.newArrayList();
    List<GEMFile> list = gemFileDb.getValues();
    for (GEMFile f : list) {
      if (f.getExtension().toLowerCase().contains(extension.toLowerCase())) {
        l.add(f);
      }
    }
    return l;
  }

  /**
   * Retrieves all stored documents It should return the minimun; file name and directory
   *
   * @return list of metadata {@link GEMFile @GEMFile}
   */
  @Override
  public List<GEMFile> getFiles() {
    List<GEMFile> l = gemFileDb.getValues();
    return l;
  }

  // Get list of local files from directory
  @Override
  public List<GEMFile> getLocalFiles(String directory) {
    File dir = new File(directory);
    List<GEMFile> resultList = Lists.newArrayList();
    File[] fList = dir.listFiles();
    if (fList != null) {
      for (File file : fList) {
        if (file.isFile()) {
          resultList.add(new GEMFile(file.getName(), file.getParent()));
        } else {
          resultList.addAll(getLocalFiles(file.getAbsolutePath()));
        }
      }
      directoryStore = directory;
    }
    return resultList;
  }

  // save file to feature store
  @Override
  public void saveFile(GEMFile file) {
    gemFileFileTypesDb.put(file.getExtension(), 1);
    gemFileDb.put(file.getAbsolutePath(), file);
  }
}
