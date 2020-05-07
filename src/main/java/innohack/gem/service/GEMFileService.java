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
  @Autowired
  private GroupService groupService;
  @Autowired
  private ExtractFileService extractFileService;

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
  public synchronized List<GEMFile> syncFiles(String folderPath) {
    LOGGER.info("Syncing from folder {}: {}", folderPath, new File(folderPath).exists());
    List<GEMFile> oldfilelist = gemFileDao.getFiles();
    List<GEMFile> newFilelist = gemFileDao.getLocalFiles(folderPath);
    if (getSyncProgress() == 1) {
      gemFileDao.setSyncStatus(0);
      extractFileService.setNewFilelist(newFilelist);
      extractFileService.setOldfilelist(oldfilelist);
      Thread syncThread = new Thread(extractFileService);
      syncThread.start();
    }
    return newFilelist;
  }

  public synchronized List<GEMFile> syncFiles1(String folderPath) {
    LOGGER.info("Syncing from folder {}: {}", folderPath, new File(folderPath).exists());
    List<GEMFile> oldfilelist = gemFileDao.getFiles();
    List<GEMFile> newFilelist = gemFileDao.getLocalFiles(folderPath);
    if (getSyncProgress() == 1) {
      gemFileDao.setSyncStatus(0);
      float totalProcesses = 0;
      float totalProcessed = 0;
      // files that does not exist in newFileList but in oldFileList
      Map<String, GEMFile> fileToDelete = new HashMap();
      // files that does not exist in oldFileList but in newFileList
      Map<String, GEMFile> fileToSave = new HashMap();

      for (GEMFile newFile : newFilelist) {
        if (!oldfilelist.contains(newFile)) {
          fileToSave.put(newFile.getAbsolutePath(), newFile);
        } else {
          oldfilelist.remove(newFile);
        }
      }

      for (GEMFile oldFile : oldfilelist) {
        fileToDelete.put(oldFile.getAbsolutePath(), oldFile);
      }

      totalProcesses =
              totalProcesses + fileToDelete.size() + fileToSave.size() + fileToSave.size() + 1;
      gemFileDao.deleteFiles(fileToDelete.keySet());
      for (GEMFile file : fileToDelete.values()) {
        matcheService.onUpdateEvent(file);
        totalProcessed = totalProcessed + 1;
        gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
      }
      gemFileDao.saveFiles(fileToSave);
      for (GEMFile file : fileToSave.values()) {
        try {
          file.extract();
        } catch (Exception ex) {
          LOGGER.debug("{}: {}", GEMFileService.class, ex.getStackTrace());
        }
        fileToSave.put(file.getAbsolutePath(), file);
        totalProcessed = totalProcessed + 1;
        gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
      }

      gemFileDao.saveFiles(fileToSave);
      for (GEMFile file : fileToSave.values()) {
        matcheService.onUpdateEvent(file);
        totalProcessed = totalProcessed + 1;
        gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
      }
      groupService.createDefaultGroup(fileToSave.values());
      gemFileDao.setSyncStatus(1);
    }
    return gemFileDao.getFiles();
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

  public float getSyncProgress() {
    float progress = gemFileDao.getSyncStatus();
    return progress;
  }
}
