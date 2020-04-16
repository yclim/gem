package innohack.gem.entity.feature;

import innohack.gem.entity.feature.common.FeatureExtractorUtil;
import java.io.File;
import org.apache.tika.config.TikaConfig;

/** Object to hold wrap extracted tika data */
public class TikaFeature extends AbstractFeature {

  @Override
  public void extract(File f) throws Exception {
    // TODO extraction method for TIKA
    TikaConfig tikaConfig = new TikaConfig();
    addMetadata("mime-type", FeatureExtractorUtil.extractMime(tikaConfig, f.toPath()).toString());
  }
}