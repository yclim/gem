package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GEMFileService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GEMFileService.class);
  @Autowired private IGEMFileDao gemFileDao;
  @Autowired private MatchService matcheService;

  public String getCurrentDirectory() {
    return gemFileDao.getCurrentDirectory();
  }

  public GEMFile getFile(String filename, String directory) {
    return gemFileDao.getFile(filename, directory);
  }

  public GEMFile getFileByAbsolutePath(String absolutePath) {
    return gemFileDao.getFileByAbsolutePath(absolutePath);
  }

  public List<GEMFile> getFileList() {
    return gemFileDao.getFiles();
  }

  /**
   * Retrieves and initial extraction for list of files in the folder
   *
   * @param folderPath directory path of files to sync
   * @return list of files that was processed and stored {@link GEMFile @GEMFile}
   */
  public List<GEMFile> syncFiles(String folderPath) throws Exception {
    LOGGER.info("Syncing from folder {}: {}", folderPath, new File(folderPath).exists());
    List<GEMFile> oldfilelist = gemFileDao.getFiles();
    List<GEMFile> newFilelist = gemFileDao.getLocalFiles(folderPath);
    Map<String, GEMFile> fileToDelete = new HashMap();
    Map<String, GEMFile> fileToSave = new HashMap();
    for (GEMFile oldFile : oldfilelist) {
      if (!newFilelist.contains(oldFile)) {
        fileToDelete.put(oldFile.getAbsolutePath(), oldFile);
      } else {
        newFilelist.remove(oldFile);
      }
    }
    for (GEMFile file : newFilelist) {
      try {
        file.extract();
      } catch (Exception ex) {
        LOGGER.debug("{}: {}", GEMFileService.class, ex.getStackTrace());
      }
      fileToSave.put(file.getAbsolutePath(), file);
    }

    gemFileDao.saveFiles(fileToSave);
    for (GEMFile file : fileToSave.values()) {
      matcheService.onUpdateEvent(file);
    }
    gemFileDao.deleteFiles(fileToDelete.keySet());
    for (GEMFile file : fileToSave.values()) {
      matcheService.onUpdateEvent(file);
    }
    return getFileList();
  }

  /**
   * Retrieves list of document metadata associated with given document name
   *
   * @param name document name
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  public List<GEMFile> findByName(String name) {
    return gemFileDao.findByName(name);
  }

  /**
   * Retrieves list of document metadata with given document file extension
   *
   * @param extension file name extension
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  public List<GEMFile> findByExtension(String extension) {
    return gemFileDao.findByExtension(extension);
  }

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  public List<String> getFileTypes() {
    return gemFileDao.getFileTypes();
  }
}
