package innohack.gem.web;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.match.MatchFileGroup;
import innohack.gem.service.GEMFileService;
import innohack.gem.service.MatchService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
public class GEMFileController {

  @Autowired private GEMFileService fileService;
  @Autowired private MatchService matchService;

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
    MatchFileGroup file = new MatchFileGroup();
    GEMFile gemfile = fileService.getFile(filename, directory);
    Map<String, MatchFileGroup> matchedFileGroups = MatchService.getMatchFileGroupTable();
    if (matchedFileGroups != null) {
      MatchFileGroup matchFileGroup = matchedFileGroups.get(gemfile.getAbsolutePath());
      if (matchFileGroup != null) {
        BeanUtils.copyProperties(matchFileGroup, file);
      }
    }
    BeanUtils.copyProperties(gemfile, file);
    return file;
  }
  // get file by absolute path
  @GetMapping("/findByAbsolutePath")
  public GEMFile getFileByAbsolutePath(@RequestParam(name = "absolutePath") String absolutePath) {
    MatchFileGroup file = new MatchFileGroup();
    GEMFile gemfile = fileService.getFileByAbsolutePath(absolutePath);
    Map<String, MatchFileGroup> matchedFileGroups = MatchService.getMatchFileGroupTable();
    if (matchedFileGroups != null) {
      MatchFileGroup matchFileGroup = matchedFileGroups.get(gemfile.getAbsolutePath());
      if (matchFileGroup != null) {
        BeanUtils.copyProperties(matchFileGroup, file);
      }
    }
    BeanUtils.copyProperties(gemfile, file);
    return file;
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
  public List<GEMFile> sync(
      @RequestParam(name = "directory", defaultValue = "/usr/share/gem/files") String directory)
      throws Exception {
    List<GEMFile> files = fileService.syncFiles(directory);
    return files;
  }

  @GetMapping("/sync/status")
  public float getSyncStatus() {
    return fileService.getSyncProgress();
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
  public List<String> getFileTypes() {
    return fileService.getFileTypes();
  }

  @RequestMapping("/noMatches")
  public Map<String, List<MatchFileGroup>> getNoMatchesFile() {
    return matchService.getMatchCount();
  }
}
