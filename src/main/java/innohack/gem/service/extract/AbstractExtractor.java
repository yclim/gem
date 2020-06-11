package innohack.gem.service.extract;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.rule.Parameter;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "extractorId")
public abstract class AbstractExtractor {

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  private String label;

  private List<Parameter> params;

  public List<Parameter> getParams() {
    return params;
  }

  public void setParams(List<Parameter> params) {
    this.params = params;
  }

  public abstract ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception;
}
