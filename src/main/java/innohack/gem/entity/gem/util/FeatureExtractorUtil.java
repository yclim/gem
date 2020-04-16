package innohack.gem.entity.gem.util;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class FeatureExtractorUtil {

  public static MediaType extractMime(TikaConfig tikaConfig, Path path) throws IOException {
    Metadata meta = new Metadata();
    meta.set(Metadata.RESOURCE_NAME_KEY, path.toString());
    MediaType mimeType = tikaConfig.getDetector().detect(TikaInputStream.get(path), meta);
    return mimeType;
  }
}
