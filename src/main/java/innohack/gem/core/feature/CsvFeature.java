package innohack.gem.core.feature;

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
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Object to hold wrap csv data */
public class CsvFeature extends AbstractFeature {

  private List<List<String>> tableData = new ArrayList<>();
  private List<String> headers = new ArrayList<>();

  @Override
  public void extract(File f) throws Exception {
    contentParser(f);
  }

  public List<List<String>> getTableData() {
    return tableData;
  }

  public void setTableData(List<List<String>> tableData) {
    this.tableData = tableData;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
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
    if (tableData.size() > 0) {
      headers = tableData.get(0);
    }
    csvReader.close();
  }

  /** use openCSV libraries instead of tika for better csv support */
  private CSVReader getCsvReaderUsingOpenCsv(Path filePath) throws IOException {
    // read:
    // https://stackoverflow.com/questions/26268132/all-inclusive-charset-to-avoid-java-nio-charset-malformedinputexception-input
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), UTF_8));
    CSVParser parser =
        new CSVParserBuilder()
            .withSeparator(detectSeparators(reader))
            .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_QUOTES)
            .withIgnoreLeadingWhiteSpace(true)
            .withIgnoreQuotations(false)
            .withStrictQuotes(false)
            .build();

    return new CSVReaderBuilder(reader).withCSVParser(parser).build();
  }

  /** detectSeparators to help determine the delimiters for a csv files */
  private char detectSeparators(BufferedReader pBuffered) throws IOException {
    int lMaxValue = 0;
    char lCharMax = ',';
    pBuffered.mark(2048);

    ArrayList<Separators> lSeparators = new ArrayList<>();
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
