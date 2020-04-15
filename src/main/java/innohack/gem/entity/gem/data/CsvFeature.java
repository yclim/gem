package innohack.gem.entity.gem.data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import innohack.gem.entity.gem.util.FeatureExtractorUtil;
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

  private List<List<String>> contents = new ArrayList<>();
  private HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
  private HashMap<Integer, String> colMapper = new HashMap<Integer, String>();
  private int totalRow = 0;
  private TikaTextAndCsvParser csvParser;

  public CsvFeature() {
    super(Target.CSV);
  }

  @Override
  public void extract(File f) throws TikaException, SAXException, IOException, CsvException {
    // TODO extraction method for CSV

    csvParser = new TikaTextAndCsvParser(f.toPath());
    // to get metadata first
    Metadata metadata = null;

      metadata = csvParser.parseMetaDataUsingTextAndCsv();

      String[] metadataNames = metadata.names();

      for (String name : metadataNames) {

        System.out.println(name + " : " + metadata.get(name));
        addMetadata(name, metadata.get(name));
      }
      contentParser(f);

  }



  private void contentParser(File f) throws IOException, CsvException {

      OpenCsvParser csvParser = new OpenCsvParser(f.toPath());
      CSVReader csvReader = csvParser.getCsvReaderUsingOpenCsv();

      if (csvReader != null) {
        // read all records at once
        List<String[]> records = null;
        records = csvReader.readAll();

        // iterate through list of records

        for (String[] record : records) {
          // this is for the records row
          ArrayList<String> recordBuilder = new ArrayList<String>();

          int colCount = 0;
          for (String cell : record) {
            recordBuilder.add(cell);
            if (totalRow == 0) {
              FeatureExtractorUtil.buildHeaderMapperAndContent(cell, colRecords,
                  colMapper, colCount);

            } else {
              FeatureExtractorUtil.buildContent(cell, colRecords, colMapper, colCount);
            }
            colCount++;
          }
          contents.add(recordBuilder);
          totalRow++;
        }
        System.out.println("Col is :" + colRecords.toString());

      } else {
        System.out.println("Not able to parse");
      }

      // close readers
      csvReader.close();

  }

  public List<List<String>> getContents() {
    return contents;
  }

  public void setContents(List<List<String>> contents) {
    this.contents = contents;
  }

  public List<String> getHeader() {
    return this.contents.get(0);
  }

}
