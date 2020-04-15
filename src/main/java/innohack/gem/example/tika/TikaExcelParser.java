package innohack.gem.example.tika;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaExcelParser {

  private Path filePath;
  private MediaType mediaType;

  public TikaExcelParser(Path filePath, MediaType mediaType) {
    System.out.println("Excel filepath is " + filePath);
    this.filePath = filePath;
    this.mediaType = mediaType;
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
    Parser parser = null;
    if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLSX.getMimeType()) ) {
      parser = new OOXMLParser();
    }else if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
      parser = new OfficeParser();
    }

    if (parser != null) {
      parser.parse(inputstream, handler, metadata, pcontext);

    } else {
      System.out.println("No sutiable parser for this type");
    }
      inputstream.close();
    return metadata;
  }

}
