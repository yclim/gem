package innohack.gem.service;

import com.beust.jcommander.internal.Lists;
import innohack.gem.dao.IExtractDao;
import innohack.gem.dao.IGEMFileDao;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.rule.Group;
import innohack.gem.service.extract.AbstractExtractor;
import innohack.gem.service.extract.ExtractorFactory;
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

  @Autowired private IGEMFileDao gemFileDao;

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
    List<GEMFile> files =
        group.getMatchedFile().stream()
            .map(f -> gemFileDao.getFile(f.getFileName(), f.getDirectory()))
            .collect(Collectors.toList());
    AbstractExtractor extractor = config.getExtractor();

    List<ExtractedFile> results = Lists.newArrayList();
    for (GEMFile file : files) {
      ExtractedRecords records = extractor.extract(file, config);
      extractDao.saveExtractedRecords(groupId, file.getAbsolutePath(), records);
      results.add(new ExtractedFile(file.getFileName(), file.getAbsolutePath(), records.size()));
    }
    LOGGER.info("Completed extraction of group {}", groupId);
    return results;
  }

  public ExtractConfig updateExtractConfig(int groupId, ExtractConfig config) {
    return extractDao.saveConfig(groupId, config);
  }

  public ExtractConfig createExtractConfig(int groupId) throws Exception {
    if (groupService.getGroups().stream().anyMatch(g -> g.getGroupId() == groupId)) {
      ExtractConfig config = new ExtractConfig();
      config.setGroupId(groupId);
      config.setExtractor(getExtractorTemplates().get(0));
      return extractDao.saveConfig(groupId, config);
    } else {
      throw new IllegalStateException("Given groupId " + groupId + " not found");
    }
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
                  extractDao.getExtractedRecords(groupId, file.getAbsolutePath());
              return new ExtractedFile(
                  file.getFileName(), file.getAbsolutePath(), records != null ? records.size() : 0);
            })
        .collect(Collectors.toList());
  }

  /**
   * Get config by group id
   *
   * @param groupId
   * @return
   */
  public ExtractConfig getExtractConfig(int groupId) {
    return extractDao.getConfig(groupId);
  }

  /**
   * Get all extractors templates
   *
   * @return list of extractors
   * @throws Exception
   */
  public List<AbstractExtractor> getExtractorTemplates() throws Exception {
    return ExtractorFactory.createAllInstance();
  }
}
