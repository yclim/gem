package innohack.gem.extractor.tika;

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
   * detectSeparators to help determine the delimiters for a csv files
   *
   * @param pBuffered bufferedread for the inputstream
   * @return character delimiter
   * @throws IOException if file is not found
   */
  private char detectSeparators(BufferedReader pBuffered) throws IOException {
    int lMaxValue = 0;
    char lCharMax = ',';
    pBuffered.mark(2048);

    ArrayList<Separators> lSeparators = new ArrayList<Separators>();
    lSeparators.add(new Separators(','));
    lSeparators.add(new Separators(';'));
    lSeparators.add(new Separators('\t'));

    Iterator<Separators> lIterator = lSeparators.iterator();
    while (lIterator.hasNext()) {
      Separators lSeparator = lIterator.next();
      au.com.bytecode.opencsv.CSVReader lReader =
          new CSVReader(pBuffered, lSeparator.getSeparator());
      String[] lLine;
      lLine = lReader.readNext();
      lSeparator.setCount(lLine.length);

      if (lSeparator.getCount() > lMaxValue) {
        lMaxValue = lSeparator.getCount();
        lCharMax = lSeparator.getSeparator();
      }
      pBuffered.reset();
    }
    System.out.println("The seperator is " + lCharMax);
    return lCharMax;
  }

  /** parsingUsingOpenCSV uses openCSV libraries instead of tika for better csv support */
  public void parseUsingOpenCsv() {

    Reader reader;
    try {
      reader = Files.newBufferedReader(this.filePath, UTF_8);

      System.out.println(" Using opencsv ");
      CSVParser parser =
          new CSVParserBuilder()
              .withSeparator(detectSeparators((BufferedReader) reader))
              .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
              .withIgnoreLeadingWhiteSpace(true)
              .withIgnoreQuotations(false)
              .withStrictQuotes(false)
              .build();

      com.opencsv.CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

      // read all records at once
      List<String[]> records = csvReader.readAll();

      // iterate through list of records
      for (String[] record : records) {
        for (String cell : record) {
          System.out.print(cell + " ");
        }
        System.out.println("\n");
      }

      // close readers
      csvReader.close();
      reader.close();

    } catch (IOException | CsvException e) {
      e.printStackTrace();
    }
  }

  /** Seperators to keep track of each delimiters count. */
  private class Separators {
    private char fSeparatorChar;
    private int fFieldCount;

    public Separators(char pSeparator) {
      fSeparatorChar = pSeparator;
    }

    public void setSeparator(char pSeparator) {
      fSeparatorChar = pSeparator;
    }

    public void setCount(int pCount) {
      fFieldCount = pCount;
    }

    public char getSeparator() {
      return fSeparatorChar;
    }

    public int getCount() {
      return fFieldCount;
    }
  }
}
