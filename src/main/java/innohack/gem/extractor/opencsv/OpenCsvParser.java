package innohack.gem.extractor.opencsv;

import static java.nio.charset.StandardCharsets.UTF_8;

import au.com.bytecode.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpenCsvParser {

  private Path filePath;

  public OpenCsvParser(Path filePath) {
    System.out.println("filepath is " + filePath);
    this.filePath = filePath;
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

  /**
   * this is to get a csvreader back using a filePath
   *
   * @return CSVReader
   */
  public com.opencsv.CSVReader getCsvReaderUsingOpenCsv() {

    Reader reader;
    com.opencsv.CSVReader csvReader = null;

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

      csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return csvReader;
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
