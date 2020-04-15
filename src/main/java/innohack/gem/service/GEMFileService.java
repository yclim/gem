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

  @Autowired private IGEMFileDao gemDao;

  public String getCurrentDirectory() {
    return gemDao.getCurrentDirectory();
  }

  public GEMFile getFile(String filename, String directory) {
    return gemDao.getFile(filename, directory);
  }

  public GEMFile getFileByAbsolutePath(String absolutePath) {
    return gemDao.getFileByAbsolutePath(absolutePath);
  }

  public Collection<GEMFile> getFileList() {
    return gemDao.getFiles();
  }

  /**
   * Retrieves and initial extraction for list of files in the folder
   *
   * @param folderPath directory path of files to sync
   * @return list of files that was processed and stored {@link GEMFile @GEMFile}
   */
  public Collection<GEMFile> syncFiles(String folderPath) {
    Collection<GEMFile> filelist = gemDao.getLocalFiles(folderPath);
    for (GEMFile file : filelist) {

      // perform extraction
      file.extract();
      gemDao.saveFile(file);

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
    return gemDao.findByName(name);
  }

  /**
   * Retrieves list of document metadata with given document file extension
   *
   * @param extension file name extension
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  public Collection<GEMFile> findByExtension(String extension) {
    return gemDao.findByExtension(extension);
  }

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  public Set<String> getFileTypes() {
    return gemDao.getFileTypes();
  }
}
