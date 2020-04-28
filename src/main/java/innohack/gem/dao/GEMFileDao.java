package innohack.gem.dao;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class GEMFileDao implements IGEMFileDao {
  private static ConcurrentHashMap<String, GEMFile> featureStore = new ConcurrentHashMap<>();
  private static String directoryStore = "";
  public static Set<String> fileTypeStore = new HashSet<String>();

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
  public Set<String> getFileTypes() {
    return fileTypeStore;
  }

  @Override
  public void deleteAll() {
    featureStore = new ConcurrentHashMap<String, GEMFile>();
  }

  @Override
  public void delete(String absolutePath) {
    featureStore.remove(absolutePath);
  }

  // This method get file data from feature store
  @Override
  public GEMFile getFile(String filename, String directory) {
    return featureStore.get(Paths.get(directory, filename).toString());
  }

  // This method get file data from feature store
  @Override
  public GEMFile getFileByAbsolutePath(String absolutePath) {
    return featureStore.get(absolutePath);
  }

  @Override
  public List<GEMFile> findByName(String filename) {
    List<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
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
    for (GEMFile f : featureStore.values()) {
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
    List<GEMFile> l = Lists.newArrayList();
    for (GEMFile f : featureStore.values()) {
      l.add(new GEMFile(f.getFileName(), f.getDirectory()));
    }
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
    fileTypeStore.add(file.getExtension());
    featureStore.put(file.getAbsolutePath(), file);
  }
}
