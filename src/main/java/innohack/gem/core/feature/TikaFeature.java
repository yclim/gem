package innohack.gem.core.feature;

import java.io.*;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

/** Object to hold wrap extracted tika data */
public class TikaFeature extends AbstractFeature {

  private String content;

  private static TikaConfig tikaConfig;

  public static TikaConfig getTikaConfig() throws Exception {
    if (tikaConfig == null) {
      tikaConfig = new TikaConfig();
    }

    return tikaConfig;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public void extract(File f) throws Exception {
    Parser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();

    try (FileInputStream inputStream = new FileInputStream(f)) {
      ParseContext context = new ParseContext();

      BodyContentHandler handler = new BodyContentHandler(-1);
      parser.parse(inputStream, handler, metadata, context);

      this.content = removeByteOrder(handler.toString(), detectEncoding(f));
    }

    String[] metadataNames = metadata.names();
    for (String name : metadataNames) {
      addMetadata(name, metadata.get(name));
    }
  }

  private Charset detectEncoding(File f) throws Exception {

    InputStream targetStream = new FileInputStream(f);
    EncodingDetector encodeDetector = this.getTikaConfig().getEncodingDetector();

    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, f.getAbsolutePath());

    return encodeDetector.detect(TikaInputStream.get(targetStream), metadata);
  }

  private String removeByteOrder(String record, Charset charset) throws Exception {
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
