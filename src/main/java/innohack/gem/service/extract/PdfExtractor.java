package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;

public class PdfExtractor extends AbstractExtractor {

  public PdfExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public ExtractedRecords extract(GEMFile f) throws Exception {
    return null;
  }
}
