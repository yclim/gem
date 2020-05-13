package innohack.gem.service.extract;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.rule.Parameter;
import java.util.List;

public abstract class AbstractExtractor {

  private List<Parameter> params;

  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

  public abstract ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception;
}
