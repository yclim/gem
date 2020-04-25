package innohack.gem.web;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.service.GEMFileService;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class GEMFileController {

  @Autowired private GEMFileService fileService;

  /**
   * Retrieves metadata for all uploaded documents
   *
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  // get list of files from feature store
  @GetMapping("/currentDir")
  public String getCurrentDirectory() {
    return fileService.getCurrentDirectory();
  }

  // get file by directory and filename
  @GetMapping("/findByNameAndDir")
  public GEMFile getFile(
      @RequestParam(name = "filename") String filename,
      @RequestParam(name = "directory") String directory) {
    return fileService.getFile(filename, directory);
  }
  // get file by absolute path
  @GetMapping("/findByAbsolutePath")
  public GEMFile getFileByAbsolutePath(@RequestParam(name = "absolutePath") String absolutePath) {
    return fileService.getFileByAbsolutePath(absolutePath);
  }

  /**
   * Retrieves metadata for all uploaded documents
   *
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  // get list of files from feature store
  @GetMapping("/list")
  public List<GEMFile> getFileList() {
    return fileService.getFileList();
  }

  // get list of files and perform data extraction
  @GetMapping("/sync")
  public List<GEMFile> sync(@RequestParam(name = "directory") String directory) throws Exception {
    if (directory.trim().length() > 0) {
      return fileService.syncFiles(directory);
    } else {
      return Lists.newArrayList();
    }
  }

  /**
   * Retrieves list of document metadata associated with given document name
   *
   * @param name document name
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  @RequestMapping("/findByName/{name:.+}")
  public List<GEMFile> findByName(@PathVariable String name) {
    return fileService.findByName(name);
  }

  /**
   * Retrieves list of document metadata with given document file extension
   *
   * @param extension file name extension getCurrentDirectory() { const url = API_DOMAIN + END_POINT
   *     + '/currentDir'; return axios.get(url); }
   * @return list of metadata {@link GEMFile @DocumentMetadata}
   */
  @RequestMapping("/findByExtension/{extension}")
  public List<GEMFile> findByExtension(@PathVariable String extension) {
    return fileService.findByExtension(extension);
  }

  /**
   * Retrieves all file extensions uploaded
   *
   * @return list of file extension
   */
  @RequestMapping("/extensions/list")
  public Set<String> getFileTypes() {
    return fileService.getFileTypes();
  }
}
