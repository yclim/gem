package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;

public abstract class AbstractExtractor {

  private ExtractConfig extractConfig;

  public ExtractConfig getExtractConfig() {
    return extractConfig;
  }

  public void setExtractConfig(ExtractConfig extractConfig) {
    this.extractConfig = extractConfig;
  }

  public abstract ExtractedRecords extract(GEMFile f) throws Exception;
}
