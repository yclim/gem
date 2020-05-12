package innohack.gem.service.extract;

import innohack.gem.entity.extractor.ExtractConfig;

public abstract class AbstractExtractor implements Extractor {

  private ExtractConfig extractConfig;

  public ExtractConfig getExtractConfig() {
    return extractConfig;
  }

  @Override
  public void setExtractConfig(ExtractConfig extractConfig) {
    this.extractConfig = extractConfig;
  }

}
