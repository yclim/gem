package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtractFileService implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractFileService.class);
    @Autowired
    private IGEMFileDao gemFileDao;
    @Autowired
    private MatchService matcheService;
    @Autowired
    private GroupService groupService;

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
}
