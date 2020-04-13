package innohack.gem.entity.gem.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import innohack.gem.example.tika.TikaTextAndCsvParser;
import innohack.gem.extractor.opencsv.OpenCsvParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

/** Object to hold wrap csv data */
public class CsvFeature extends AbstractFeature {

  private List<String> header = new ArrayList<String>();
  private ArrayList<ArrayList<String>> contents = new ArrayList<ArrayList<String>>();
  private HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
  private HashMap<Integer, String> colMapper = new HashMap<Integer, String>();
  private int totalRow = 0;
  private TikaTextAndCsvParser csvParser;

  public CsvFeature() {
    super(Target.CSV);
  }

  @Override
  public void extract(File f) {
    // TODO extraction method for CSV

    csvParser = new TikaTextAndCsvParser(f.toPath());
    // to get metadata first
    Metadata metadata = null;
    try {
      metadata = csvParser.parseMetaDataUsingTextAndCsv();

      String[] metadataNames = metadata.names();

      for (String name : metadataNames) {

        System.out.println(name + " : " + metadata.get(name));
        addMetadata(name, metadata.get(name));
      }
      contentParser(f);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    }
  }

  private void contentParser(File f) {

    try {
      OpenCsvParser csvParser = new OpenCsvParser(f.toPath());
      CSVReader csvReader = csvParser.getCsvReaderUsingOpenCsv();

      if (csvReader != null) {
        // read all records at once
        List<String[]> records = null;
        records = csvReader.readAll();

        // iterate through list of records
        for (String[] record : records) {
          if (totalRow == 0) {
            // this is for the header row
            int colCount = 0;
            for (String cell : record) {
              header.add(cell);

              if (!colRecords.containsKey(cell)) {
                colRecords.put(cell, new ArrayList<String>());
                colMapper.put(colCount, cell);
                colCount++;
              }

              System.out.print(cell + " ");
            }
            totalRow++;
          } else {

            // this is for the records row
            ArrayList<String> recordBuilder = new ArrayList<String>();
            int colCount = 0;

            for (String cell : record) {
              recordBuilder.add(cell);

              if (colMapper.containsKey(colCount)) {
                String colHeader = colMapper.get(colCount);

                if (colRecords.containsKey(colHeader)) {
                  ArrayList<String> colCell = colRecords.get(colHeader);
                  colCell.add(cell);
                  colRecords.replace(colHeader, colCell);
                  System.out.print(cell + " ");
                }
              }
              colCount++;
            }
            contents.add(recordBuilder);
            totalRow++;
          }
        }
        System.out.println("\n");

      } else {
        System.out.println("Not able to parse");
      }

      // close readers
      csvReader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (CsvException e) {
      e.printStackTrace();
    }
  }

  public List<String> getHeader() {
    return header;
  }

  public void setHeader(List<String> header) {
    this.header = header;
  }

  public ArrayList<ArrayList<String>> getContents() {
    return contents;
  }

  public void setContents(ArrayList<ArrayList<String>> contents) {
    this.contents = contents;
  }

  public HashMap<String, ArrayList<String>> getColRecords() {
    return colRecords;
  }

  public void setColRecords(HashMap<String, ArrayList<String>> colRecords) {
    this.colRecords = colRecords;
  }

  public int getTotalRow() {
    return totalRow;
  }

  public void setTotalRow(int totalRow) {
    this.totalRow = totalRow;
  }
}
