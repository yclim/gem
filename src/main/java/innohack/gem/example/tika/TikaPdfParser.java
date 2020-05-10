package innohack.gem.example.tika;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class TikaPdfParser {

  private Path filePath;
  private static final Logger LOGGER = LoggerFactory.getLogger(TikaPdfParser.class);

  public TikaPdfParser(Path filePath) {
    this.filePath = filePath;
  }

  /**
   * parsePDF to call tika pdf parser to parse through and extract both content and metadata
   *
   * @throws IOException IOException when dealing with files
   * @throws SAXException SAX tika exception
   * @throws TikaException any tiak exception found
   */
  public void parsePDF() throws IOException, TikaException, SAXException {

    // this is to set the limit to unlimited
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(this.filePath.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    // parsing the document using PDF parser
    PDFParser pdfparser = new PDFParser();
    pdfparser.parse(inputstream, handler, metadata, pcontext);

    // getting the content of the document
    LOGGER.debug("Contents of the PDF :" + handler.toString());

    // getting metadata of the document
    // System.out.println("Metadata of the PDF:");
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      LOGGER.debug(name + " : " + metadata.get(name));
    }
    inputstream.close();
  }
}
