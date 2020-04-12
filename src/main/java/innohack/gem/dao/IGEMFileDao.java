package innohack.gem.dao;

import innohack.gem.entity.GEMFile;
import java.util.Collection;

/**
 * Allow for injecting different DAO implementations... e.g. in-memory, database, es, etc
 *
 * @author TC
 */
public interface IGEMFileDao {
  /**
   * Find document in feature store by file name and directory
   *
   * @param filename document file name
   * @param directory directory where the file is
   * @return list of document metadata
   */
  GEMFile getFile(String filename, String directory);

  /**
   * Find document in feature store by absolute path
   *
   * @param absolutePath of the document
   * @return list of document metadata
   */
  GEMFile getFileByAbsolutePath(String absolutePath);

  /**
   * Find document metadata by document file extension
   *
   * @param filename document file extension
   * @return list of document metadata
   */
  Collection<GEMFile> findByName(String filename);

  /**
   * Find document metadata by document file extension
   *
   * @param extension document file extension
   * @return list of document metadata
   */
  Collection<GEMFile> findByExtension(String extension);

  /**
   * Retrieves metadata for all stored documents It should return the minimun; file name and
   * directory
   *
   * @return list of metadata {@link GEMFile @GEMFile}
   */
  Collection<GEMFile> getFiles();

  // Get list of local files from directory
  Collection<GEMFile> getLocalFiles(String directory);

  // save file to feature store
  void saveFile(GEMFile files);
}
