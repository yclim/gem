package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import java.util.List;

public class PdfExtractor extends AbstractExtractor {

  public PdfExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public List<List<String>> extract(GEMFile f) throws Exception {
    return null;
  }
}
