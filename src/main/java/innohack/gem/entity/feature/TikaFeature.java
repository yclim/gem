package innohack.gem.entity.feature;

import java.io.File;
import java.io.FileInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

/** Object to hold wrap extracted tika data */
public class TikaFeature extends AbstractFeature {

  private String content;

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
      this.content = handler.toString();
    }

    String[] metadataNames = metadata.names();
    for (String name : metadataNames) {
      addMetadata(name, metadata.get(name));
    }
  }
}
