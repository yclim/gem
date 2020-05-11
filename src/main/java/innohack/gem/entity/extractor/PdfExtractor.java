package innohack.gem.entity.extractor;

import innohack.gem.entity.GEMFile;
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
