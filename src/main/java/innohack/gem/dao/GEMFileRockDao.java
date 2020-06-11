package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.database.RocksDatabase;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class GEMFileRockDao implements IGEMFileDao {

  private static final String DB_NAME = "GEMFile";
  private RocksDatabase<String, GEMFile> gemFileDb;
  private RocksDatabase<String, Integer> gemFileFileTypesDb;
  private RocksDatabase<String, String> gemFileState;
  private static final float SYNC_COMPLETE = 1;

  // Get current directory path where files uploaded
  public GEMFileRockDao() {
    gemFileDb = RocksDatabase.getInstance(DB_NAME, String.class, GEMFile.class);
    gemFileFileTypesDb =
        RocksDatabase.getInstance(DB_NAME + "_FileType", String.class, Integer.class);
    gemFileState = RocksDatabase.getInstance(DB_NAME + "_FileState", String.class, String.class);
    gemFileState.put(STATE_SYNC_PROGRESS, String.valueOf(SYNC_COMPLETE));
  }

  /**
   * Find document in feature store by file name and directory
   *
   * @return directory path
   */
  @Override
  public String getCurrentDirectory() {
    return (String) gemFileState.get(STATE_SYNC_DIRECTORY);
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

  /** Batch file delete to reduce Db open and close overhead */
  @Override
  public void deleteFiles(Collection<String> absolutePaths) {
    gemFileDb.delete(absolutePaths);
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
    List<String> l = gemFileDb.getKeys();
    List<GEMFile> list = Lists.newArrayList();
    for (String path : l) {
      list.add(new GEMFile(path));
    }
    return list;
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
      gemFileState.put(STATE_SYNC_DIRECTORY, directory);
    }
    return resultList;
  }

  private static List<String> types = null;
  // save file to feature store
  @Override
  public void saveFile(GEMFile file) {
    if (types == null) {
      types = gemFileFileTypesDb.getKeys();
    }
    if (!types.contains(file.getExtension())) {
      types.add(file.getExtension());
      gemFileFileTypesDb.put(file.getExtension(), 1);
    }
    gemFileDb.put(file.getAbsolutePath(), file);
  }

  /** Batch filesave to reduce Db open and close overhead */
  @Override
  public void saveFiles(Map<String, GEMFile> filesMap) {
    Map<String, Integer> fileTypesMap = new HashMap<String, Integer>();
    if (types == null) {
      types = gemFileFileTypesDb.getKeys();
    }
    for (GEMFile file : filesMap.values()) {
      if (!types.contains(file.getExtension())) {
        types.add(file.getExtension());
        fileTypesMap.put(file.getExtension(), 1);
      }
    }
    gemFileDb.putHashMap(filesMap);
    gemFileFileTypesDb.putHashMap(fileTypesMap);
  }

  private static final String STATE_SYNC_PROGRESS = "sync.progress";
  private static final String STATE_SYNC_DIRECTORY = "sync.directory";

  /**
   * get sync progress, 0-1 where 1 means sync complete
   *
   * @return syncStatus
   */
  public float getSyncStatus() {
    Object o = gemFileState.get(STATE_SYNC_PROGRESS);
    if (o == null) {
      return 1;
    }
    float process = 1;
    try {
      process = Float.parseFloat((String) o);
    } catch (Exception e) {

    }
    return process;
  }

  /**
   * set the sync progress
   *
   * @param syncStatus: float
   */
  public void setSyncStatus(float syncStatus) {
    gemFileState.put(STATE_SYNC_PROGRESS, Float.toString(syncStatus));
    // gemFileState.put(STATE_SYNC_TIME, System.currentTimeMillis() + "");
  }
}
