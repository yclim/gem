package innohack.gem.entity.feature;

import innohack.gem.entity.feature.common.FeatureExtractorUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted tika data */
public class TikaFeature extends AbstractFeature {
  private Metadata metaData;
  private BodyContentHandler handler = new BodyContentHandler(-1);

  @Override
  public void extract(File f) throws Exception {
    // TODO extraction method for TIKA
    TikaConfig tikaConfig = new TikaConfig();
    addMetadata("mime-type", FeatureExtractorUtil.extractMime(tikaConfig, f.toPath()).toString());
    this.metaData = extractTikaMetadata(f);

    String[] metadataNames = this.metaData.names();

    for (String name : metadataNames) {
      addMetadata(name, this.metaData.get(name));
    }

  }

  public Metadata extractTikaMetadata(File f) throws TikaException, SAXException, IOException  {

    Parser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();   //empty metadata object
    FileInputStream inputStream = new FileInputStream(f);
    ParseContext context = new ParseContext();
    parser.parse(inputStream, handler, metadata, context);

// now this metadata object contains the extracted metadata of the given file.
    // metadata.names();
    inputStream.close();

    return metadata;

  }

  public String parseContent() {
    return handler.toString();
  }



}
