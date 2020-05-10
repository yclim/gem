package innohack.gem.example.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.csv.TextAndCSVParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class TikaTextAndCsvParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaTextAndCsvParser.class);
  private Path filePath;

  public TikaTextAndCsvParser(Path filePath) {
    LOGGER.debug("filepath is " + filePath);
    this.filePath = filePath;
  }

  /**
   * parseTextAndCsv to call tikacsv parser, however this method do not detect seperators, so as of
   * now put as placeholder first
   *
   * @throws IOException IOexception if path is not found
   * @throws SAXException SAXException from Tika
   * @throws TikaException TikaException if thrown internal by Tika
   */
  public void parseTextAndCsv() throws IOException, SAXException, TikaException {
    // detecting the file type
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(this.filePath.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    // Text document parser
    TextAndCSVParser csvParser = new TextAndCSVParser();
    csvParser.parse(inputstream, handler, metadata, pcontext);

    LOGGER.debug("Contents of the document:" + handler.toString());
    LOGGER.debug("Metadata of the document:");
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      LOGGER.debug(name + " : " + metadata.get(name));
    }
  }

  /**
   * parseMetaDataUsingTextAndCsv to extract metadata of csv files
   *
   * @throws IOException IOexception if path is not found
   * @throws SAXException SAXException from Tika
   * @throws TikaException TikaException if thrown internal by Tika
   */
  public Metadata parseMetaDataUsingTextAndCsv() throws IOException, SAXException, TikaException {
    // detecting the file type
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(this.filePath.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    // Text document parser
    TextAndCSVParser csvParser = new TextAndCSVParser();
    csvParser.parse(inputstream, handler, metadata, pcontext);

    // LOGGER.debug("Contents of the document:" + handler.toString());
    // LOGGER.debug("Metadata of the document:");
    inputstream.close();
    return metadata;
  }
}
