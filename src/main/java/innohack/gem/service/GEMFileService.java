package innohack.gem.service;

import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.service.event.EventListener;
import innohack.gem.service.event.NewEvent;
import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GEMFileService extends NewEvent {

  @Autowired private IGEMFileDao gemFileDao;

  public String getCurrentDirectory() {
    return gemFileDao.getCurrentDirectory();
  }

  public GEMFile getFile(String filename, String directory) {
    return gemFileDao.getFile(filename, directory);
  }

  public GEMFile getFileByAbsolutePath(String absolutePath) {
    return gemFileDao.getFileByAbsolutePath(absolutePath);
  }

  public Collection<GEMFile> getFileList() {
    return gemFileDao.getFiles();
  }

  /**
   * Retrieves and initial extraction for list of files in the folder
   *
   * @param folderPath directory path of files to sync
   * @return list of files that was processed and stored {@link GEMFile @GEMFile}
   */
  public Collection<GEMFile> syncFiles(String folderPath) throws Exception {
    Collection<GEMFile> filelist = gemFileDao.getLocalFiles(folderPath);
    for (GEMFile file : filelist) {

      // perform extraction
      file.extract();
      gemFileDao.saveFile(file);

      // Trigger Matching
      newEvent(EventListener.Event.NEW_FILE, file);
    }
    return getFileList();
  }

  /**
   * Retrieves list of document metadata associated with given document name
   *
   * @param name document name
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  public Collection<GEMFile> findByName(String name) {
    return gemFileDao.findByName(name);
  }

  /**
   * Retrieves list of document metadata with given document file extension
   *
   * @param extension file name extension
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  public Collection<GEMFile> findByExtension(String extension) {
    return gemFileDao.findByExtension(extension);
  }

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  public Set<String> getFileTypes() {
    return gemFileDao.getFileTypes();
  }
}
