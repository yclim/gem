package innohack.gem.dao;

import innohack.gem.entity.GEMFile;
import java.util.List;

/**
 * Allow for injecting different DAO implementations... e.g. in-memory, database, es, etc
 *
 * @author TC
 */
public interface IGEMFileDao {

  // Get current directory path where files uploaded
  /**
   * Find document in feature store by file name and directory
   *
   * @return directory path
   */
  String getCurrentDirectory();
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
  List<GEMFile> findByName(String filename);

  /**
   * Find document metadata by document file extension
   *
   * @param extension document file extension
   * @return list of document metadata
   */
  List<GEMFile> findByExtension(String extension);

  /**
   * Retrieves all stored documents It should return the minimun; file name and directory
   *
   * @return list of metadata {@link GEMFile @GEMFile}
   */
  List<GEMFile> getFiles();

  // Get list of local files from directory
  List<GEMFile> getLocalFiles(String directory);

  // save file to feature store
  void saveFile(GEMFile files);

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  List<String> getFileTypes();

  /** Clear all file stored files */
  void deleteAll();

  /** delete file by absolutePath */
  void delete(String absolutePath);
}
