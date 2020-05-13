package innohack.gem.service;

import com.beust.jcommander.internal.Lists;
import innohack.gem.dao.IExtractDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.rule.Group;
import innohack.gem.service.extract.Extractor;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExtractService.class);

  @Autowired private GroupService groupService;

  @Autowired private IExtractDao extractDao;

  /**
   * Perform an extraction
   *
   * @param groupId
   * @return
   * @throws Exception
   */
  public List<ExtractedFile> extract(int groupId) throws Exception {
    LOGGER.info("Performing extraction of group {}...", groupId);
    Group group = groupService.getGroup(groupId);
    ExtractConfig config = extractDao.getConfig(groupId);
    List<GEMFile> files = group.getMatchedFile();
    Extractor extractor = config.getExtractor();
    extractor.setExtractConfig(config);

    List<ExtractedFile> results = Lists.newArrayList();
    for (GEMFile file : files) {
      ExtractedRecords records = extractor.extract(file);
      extractDao.saveExtractedRecords(groupId, file.getFileName(), records);
      results.add(new ExtractedFile(file.getFileName(), records.size()));
    }
    LOGGER.info("Completed extraction of group {}", groupId);
    return results;
  }

  public ExtractConfig updateExtractConfig(int groupId, ExtractConfig config) {
    return extractDao.saveConfig(groupId, config);
  }

  /**
   * Obtain the results of the previous extraction
   *
   * @param groupId
   * @param filename
   * @return
   */
  public ExtractedRecords getExtractedRecords(int groupId, String filename) {
    return extractDao.getExtractedRecords(groupId, filename);
  }

  /**
   * Obtain the aggregated results (by file) of the previous extraction
   *
   * @param groupId
   * @return
   */
  public List<ExtractedFile> getExtractedFiles(int groupId) {
    Group group = groupService.getGroup(groupId);
    return group.getMatchedFile().stream()
        .map(
            file -> {
              ExtractedRecords records =
                  extractDao.getExtractedRecords(groupId, file.getFileName());
              return new ExtractedFile(file.getFileName(), records.size());
            })
        .collect(Collectors.toList());
  }

}
