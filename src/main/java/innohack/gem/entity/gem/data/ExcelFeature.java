package innohack.gem.entity.gem.data;

import java.io.File;

/** Object to hold wrap extracted excel data */
public class ExcelFeature extends AbstractFeature {
  public ExcelFeature() {
    super(Target.EXCEL);
  }

  @Override
  public void extract(File f) {
    // TODO extraction method for EXCEL
  }
}
