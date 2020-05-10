package innohack.gem.example.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class TikaTextParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaTextParser.class);
  /**
   * parseText use tika to parse through pure text
   *
   * @param filepath of the folder path
   * @throws IOException IOException when dealing with files
   * @throws SAXException SAX tika exception
   * @throws TikaException any tiak exception found
   */
  public static void parseText(Path filepath) throws IOException, SAXException, TikaException {
    // detecting the file type
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream = new FileInputStream(new File("example.txt"));
    ParseContext pcontext = new ParseContext();

    // Text document parser
    TXTParser textParser = new TXTParser();
    textParser.parse(inputstream, handler, metadata, pcontext);
    LOGGER.debug("Contents of the document:" + handler.toString());
    LOGGER.debug("Metadata of the document:");
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      LOGGER.debug(name + " : " + metadata.get(name));
    }
    inputstream.close();
  }
}
