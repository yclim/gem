package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExtractor.class);
  private ExtractConfig extractConfig;

  public ExtractConfig getExtractConfig() {
    return extractConfig;
  }

  public void setExtractConfig(ExtractConfig extractConfig) {
    this.extractConfig = extractConfig;
  }

  public abstract ExtractedRecords extract(GEMFile f) throws Exception;
}
