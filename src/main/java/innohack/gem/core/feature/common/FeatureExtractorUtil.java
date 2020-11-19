package innohack.gem.core.feature.common;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class FeatureExtractorUtil {

  private static TikaConfig tikaConfig;

  public static TikaConfig getTikaConfig() throws IOException, TikaException {
    if (tikaConfig == null) {
      tikaConfig = new TikaConfig();
    }

    return tikaConfig;
  }

  public MediaType extractMime(TikaConfig tikaConfig, Path path) throws IOException {
    Metadata meta = new Metadata();
    meta.set(Metadata.RESOURCE_NAME_KEY, path.toString());
    MediaType mimeType = tikaConfig.getDetector().detect(TikaInputStream.get(path), meta);
    return mimeType;
  }

  public Charset detectEncoding(File f) throws IOException, TikaException {

    InputStream targetStream = new FileInputStream(f);
    EncodingDetector encodeDetector = this.getTikaConfig().getEncodingDetector();

    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, f.getAbsolutePath());

    return encodeDetector.detect(TikaInputStream.get(targetStream), metadata);
  }

  public String removeByteOrder(String record, Charset charset) throws IOException {
    byte[] bytes = record.getBytes(charset);

    InputStream inputStream = new ByteArrayInputStream(bytes);

    BOMInputStream bomInput = new BOMInputStream(inputStream, false);

    if (bomInput.hasBOM()) {
      String rtnResult = IOUtils.toString(bomInput, charset);

      return rtnResult;
    } else {
      return record;
    }
  }
}
