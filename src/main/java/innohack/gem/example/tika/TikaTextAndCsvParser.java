package innohack.gem.example.tika;

import static java.nio.charset.StandardCharsets.UTF_8;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.core.util.Separators;
import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.csv.TextAndCSVParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class TikaTextAndCsvParser {

  private Path filePath;

  public TikaTextAndCsvParser(Path filePath) {
    System.out.println("filepath is " + filePath);
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

    System.out.println("Contents of the document:" + handler.toString());
    System.out.println("Metadata of the document:");
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      System.out.println(name + " : " + metadata.get(name));
    }
  }

  /**
   * parseTextAndCsv to call tikacsv parser, however this method do not detect seperators, so as of
   * now put as placeholder first
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

    // System.out.println("Contents of the document:" + handler.toString());
    // System.out.println("Metadata of the document:");
    inputstream.close();
    return metadata;
  }


}
