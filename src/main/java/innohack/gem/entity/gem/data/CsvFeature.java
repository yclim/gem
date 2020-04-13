package innohack.gem.entity.gem.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import innohack.gem.example.tika.TikaTextAndCsvParser;
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

  private String header;
  private ArrayList<String> contents = new ArrayList<String>();
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
      csvContentParser();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    }
  }

  private void csvContentParser() {

    try {
      CSVReader csvReader = csvParser.getCsvReaderUsingOpenCsv();

      if (csvReader != null) {
        // read all records at once
        List<String[]> records = null;
        records = csvReader.readAll();

        // iterate through list of records
        for (String[] record : records) {
          if (totalRow == 0) {
            // this is for the header row
            StringBuilder headerBuilder = new StringBuilder();
            int colCount = 0;
            for (String cell : record) {
              headerBuilder.append(cell);
              headerBuilder.append(";");

              if (!colRecords.containsKey(cell)) {
                colRecords.put(cell, new ArrayList<String>());
                colMapper.put(colCount, cell);
                colCount++;
              }

              System.out.print(cell + " ");
            }
            header = headerBuilder.toString();
            header = header.substring(0, header.length() - 1);
            totalRow++;
          } else {

            // this is for the records row
            StringBuilder recordBuilder = new StringBuilder();
            int colCount = 0;

            for (String cell : record) {
              recordBuilder.append(cell);
              recordBuilder.append(";");

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
            String perRow = recordBuilder.toString();
            perRow = perRow.substring(0, perRow.length() - 1);
            contents.add(perRow);
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

  public ArrayList<String> getContents() {
    return contents;
  }

  public void setContents(ArrayList<String> contents) {
    this.contents = contents;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
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
