package innohack.gem.service;

import com.beust.jcommander.internal.Maps;
import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.dao.IGEMFileDao;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncFileService implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SyncFileService.class);
  @Autowired private IGEMFileDao gemFileDao;
  @Autowired private MatchService matcheService;
  @Autowired private GroupService groupService;

  List<GEMFile> newFilelist;
  List<GEMFile> oldfilelist;

  public void setNewFilelist(List<GEMFile> newFilelist) {
    this.newFilelist = newFilelist;
  }

  public void setOldfilelist(List<GEMFile> oldfilelist) {
    this.oldfilelist = oldfilelist;
  }

  @Override
  public void run() {
    float totalProcesses = 0;
    float totalProcessed = 0;
    // files that does not exist in newFileList but in oldFileList
    Map<String, GEMFile> fileToDelete = Maps.newHashMap();
    // files that does not exist in oldFileList but in newFileList
    Map<String, GEMFile> fileToSave = Maps.newHashMap();

    // gather all the newly seen file and put in fileToSave and remove files that was already in
    // database from oldFileList
    for (GEMFile newFile : newFilelist) {
      if (!oldfilelist.contains(newFile)) {
        fileToSave.put(newFile.getAbsolutePath(), newFile);
      } else {
        oldfilelist.remove(newFile);
      }
    }
    // save newly seen file in Db, so that it will appear in ui when user refresh screen
    gemFileDao.saveFiles(fileToSave);

    // get all the files that are not in the directory anymore and place in fileToDelete
    for (GEMFile oldFile : oldfilelist) {
      fileToDelete.put(oldFile.getAbsolutePath(), oldFile);
    }

    // Count the number of operations needed to finish sync. Will be used for loading progress
    totalProcesses =
        totalProcesses + fileToDelete.size() + fileToSave.size() + fileToSave.size() + 1;

    // Delete files in database and notify matchservice
    LOGGER.info("Clear previous state...");
    gemFileDao.deleteFiles(fileToDelete.keySet());
    for (GEMFile file : fileToDelete.values()) {
      matcheService.onUpdateEvent(file);
      totalProcessed = totalProcessed + 1;
      gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
    }

    // Extract data from new files and save to database
    LOGGER.info("Extracting files...");
    for (GEMFile file : fileToSave.values()) {
      GEMFile result = file;
      try {
        File f = new File(file.getAbsolutePath());
        result = GEMMain.extractFeature(f);
      } catch (Exception ex) {
        LOGGER.debug("{}: {}", GEMFileService.class, ex.getStackTrace());
      }
      fileToSave.put(file.getAbsolutePath(), result);
      totalProcessed = totalProcessed + 1;
      gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
    }
    LOGGER.info("Saving files...");
    gemFileDao.saveFiles(fileToSave);

    // Notify matchservice on the extracted files.
    for (GEMFile file : fileToSave.values()) {
      matcheService.onUpdateEvent(file);
      totalProcessed = totalProcessed + 1;
      gemFileDao.setSyncStatus(totalProcessed / totalProcesses);
    }

    // Create default groups based on the newly added files
    LOGGER.info("Saving groups...");
    groupService.createDefaultGroup(fileToSave.values());

    // set status to complete
    gemFileDao.setSyncStatus(1);
  }
}
