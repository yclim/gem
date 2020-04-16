package innohack.gem.entity.gem.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import au.com.bytecode.opencsv.CSVReader;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class FeatureExtractorUtil {

  public static void buildHeaderMapperAndContent(
      String cell,
      HashMap<String, ArrayList<String>> colRecords,
      HashMap<Integer, String> colMapper,
      int colCount) {

    if (!colRecords.containsKey(cell)) {
      colRecords.put(cell, new ArrayList<String>());
      colMapper.put(colCount, cell);
    }
  }

  public static void buildContent(
      String cell,
      HashMap<String, ArrayList<String>> colRecords,
      HashMap<Integer, String> colMapper,
      int colCount) {

    if (colMapper.containsKey(colCount)) {
      String colHeader = colMapper.get(colCount);

      if (colRecords.containsKey(colHeader)) {
        ArrayList<String> colCell = colRecords.get(colHeader);
        colCell.add(cell);
        colRecords.replace(colHeader, colCell);
      }
    }
  }

  public static String cellValue(Cell cell) {

    String rtnValue = "";
    if (cell.getCellType() == CellType.NUMERIC) {
      rtnValue = ((int) cell.getNumericCellValue()) + "";
    } else if (cell.getCellType() == CellType.BOOLEAN) {
      rtnValue = cell.getBooleanCellValue() + "";
    } else if (cell.getCellType() == CellType.STRING) {
      rtnValue = cell.getStringCellValue();
    } else if (cell.getCellType() == CellType._NONE || cell.getCellType() == CellType.BLANK) {
      rtnValue = "";
    } else {
      rtnValue = "";
    }
    return rtnValue;
  }

  /** parsingUsingOpenCSV uses openCSV libraries instead of tika for better csv support */
  public static com.opencsv.CSVReader getCsvReaderUsingOpenCsv(Path filePath) {

    Reader reader;
    com.opencsv.CSVReader csvReader = null;

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

  /**
   * detectSeparators to help determine the delimiters for a csv files
   *
   * @param pBuffered bufferedread for the inputstream
   * @return character delimiter
   * @throws IOException if file is not found
   */
  private static char detectSeparators(BufferedReader pBuffered) throws IOException {
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

  public static MediaType extractMime(TikaConfig tikaConfig, Path path) throws IOException {
    Metadata meta = new Metadata();
    meta.set(Metadata.RESOURCE_NAME_KEY, path.toString());
    MediaType mimeType = tikaConfig.getDetector().detect(TikaInputStream.get(path), meta);
    return mimeType;
  }
}

/** Seperators to keep track of each delimiters count. */
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
