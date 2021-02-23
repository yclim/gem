package innohack.gem.service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beust.jcommander.internal.Lists;
import com.opencsv.CSVWriter;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedFile;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.extract.AbstractExtractor;
import innohack.gem.core.extract.ExtractorFactory;
import innohack.gem.dao.IExtractDao;
import innohack.gem.dao.IGEMFileDao;

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

  public void export(int groupId, OutputStream output) throws Exception {
	  List<ExtractedFile> extractions = extract(groupId);
	  try(ZipOutputStream out = new ZipOutputStream(output);) {
		  for(ExtractedFile extraction: extractions) {
			  //write each result as a file inside the zip
			  LOGGER.info("Extracting and exporting {}", extraction.getFilename());
			  ExtractedRecords records = getExtractedRecords(groupId, extraction.getAbsolutePath());
			  
			  ZipEntry e = new ZipEntry(extraction.getFilename()+".txt");
			  out.putNextEntry(e);
			  
			  CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));
		      writer.writeNext(toArray(records.getHeaders())); //write header
		      for(List<String> row: records.getRecords()) {
		    	  writer.writeNext(toArray(row));
		      }
		      writer.flush();
			  out.closeEntry();
		  }
	  }
  }
  
  private String[] toArray(List<String> values) {
	  return values.toArray(new String[values.size()]);
  }
}
