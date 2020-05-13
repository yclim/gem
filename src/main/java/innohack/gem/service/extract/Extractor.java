package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;

public interface Extractor {

  void setExtractConfig(ExtractConfig extractConfig);
  
  ExtractedRecords extract(GEMFile f) throws Exception;
  
}
