package innohack.gem.example.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaExcelParser {

  private Path filePath;

  public TikaExcelParser(Path filePath) {
    System.out.println("filepath is " + filePath);
    this.filePath = filePath;
  }

  /**
   * parseExcel to extract metadata of csv files
   *
   * @throws IOException IOexception if path is not found
   * @throws TikaException TikaException if thrown internal by Tika
   */
  public Metadata parseExcel() throws IOException, TikaException, SAXException {

    // detecting the file type
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(this.filePath.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    // OOXml parser
    OOXMLParser msofficeparser = new OOXMLParser();
    msofficeparser.parse(inputstream, handler, metadata, pcontext);
    System.out.println("Contents of the document:" + handler.toString());
    System.out.println("Metadata of the document:");
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      System.out.println(name + ": " + metadata.get(name));
    }
    inputstream.close();
    return metadata;
  }
}
