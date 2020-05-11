package innohack.gem.service;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedFile;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.rule.Group;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExtractService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ExtractService.class);
  
  @Autowired
  private GroupService groupService;

  public List<ExtractedFile> extract(int groupId) {
    Group group = groupService.getGroup(groupId);
    ExtractConfig config = getConfig(groupId);
    List<GEMFile> files = group.getMatchedFile();
    LOGGER.info("TODO: I am suppose to parse the files with the extractors specified by config?");
    return null;
  }

  public ExtractConfig updateExtractConfig(int groupId, ExtractConfig config) {
    // TODO Auto-generated method stub
    return null;
  }

  public ExtractedRecords extractRecords(int groupId, String filename) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private ExtractConfig getConfig(int groupId) {
  //XXX Hmmm somewhere we need a handle on getting the ExtractConfig...
    return null;
  }

}
