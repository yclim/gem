package innohack.gem.entity.gem.data;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
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

/** Object to hold wrap csv data */
public class CsvFeature extends AbstractFeature {

  private List<List<String>> tableData = new ArrayList<>();

  @Override
  public void extract(File f) throws Exception {
    Metadata metadata = null;

    metadata = parseMetaDataUsingTextAndCsv(f.toPath());

    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      addMetadata(name, metadata.get(name));
    }

    contentParser(f);
  }

  private void contentParser(File f) throws IOException, CsvException {

    CSVReader csvReader = getCsvReaderUsingOpenCsv(f.toPath());

    List<String[]> records = null;
    records = csvReader.readAll();

    for (String[] record : records) {
      List<String> recordBuilder = new ArrayList<>();
      for (String cell : record) {
        recordBuilder.add(cell);
      }
      tableData.add(recordBuilder);
    }

    csvReader.close();
  }

  public Metadata parseMetaDataUsingTextAndCsv(Path filePath)
      throws IOException, SAXException, TikaException {
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(filePath.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    TextAndCSVParser csvParser = new TextAndCSVParser();
    csvParser.parse(inputstream, handler, metadata, pcontext);

    inputstream.close();
    return metadata;
  }

  /** use openCSV libraries instead of tika for better csv support */
  private CSVReader getCsvReaderUsingOpenCsv(Path filePath) {
    Reader reader;
    CSVReader csvReader = null;

    try {
      reader = Files.newBufferedReader(filePath, UTF_8);

      System.out.println(" Using opencsv ");
      CSVParser parser =
          new CSVParserBuilder()
              .withSeparator(detectSeparators((BufferedReader) reader))
              .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
              .withIgnoreLeadingWhiteSpace(true)
              .withIgnoreQuotations(false)
              .withStrictQuotes(false)
              .build();

      csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return csvReader;
  }

  /** detectSeparators to help determine the delimiters for a csv files */
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
          new au.com.bytecode.opencsv.CSVReader(pBuffered, lSeparator.getSeparator());
      String[] lLine;
      lLine = lReader.readNext();
      lSeparator.setCount(lLine.length);

      if (lSeparator.getCount() > lMaxValue) {
        lMaxValue = lSeparator.getCount();
        lCharMax = lSeparator.getSeparator();
      }
      pBuffered.reset();
    }
    return lCharMax;
  }

  public List<List<String>> getTableData() {
    return tableData;
  }

  public List<String> getHeaders() {
    return this.tableData.get(0);
  }
}

class Separators {
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
