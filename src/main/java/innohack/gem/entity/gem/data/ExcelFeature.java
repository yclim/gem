package innohack.gem.entity.gem.data;

import innohack.gem.entity.gem.util.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaExcelParser;
import innohack.gem.extractor.poi.PoiExcelParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted excel data */
public class ExcelFeature extends AbstractFeature {

  private TikaExcelParser excelParser;
  private PoiExcelParser poiParser;
  private Workbook workbook;
  private Map<String, List<List<String>>> sheetFeatures;
  private MediaType mediaType;

  public ExcelFeature(MediaType mediaType) {
    super(Target.EXCEL);
    this.mediaType = mediaType;

  }


  public Workbook getWorkbook() {
    return workbook;
  }

  public void setWorkbook(Workbook workbook) {
    this.workbook = workbook;
  }

  public Map<String, List<List<String>>> getSheetFeatures() {
    return sheetFeatures;
  }

  public void setSheetFeatures(
      Map<String, List<List<String>>> sheetFeatures) {
    this.sheetFeatures = sheetFeatures;
  }

  @Override
  public void extract(File f) throws IOException, TikaException, SAXException {
    // TODO extraction method for EXCEL
    // TODO extraction method for EXCEL

    excelParser = new TikaExcelParser(f.toPath(), mediaType);
    poiParser = new PoiExcelParser(f.toPath());
    workbook = poiParser.getWorkBook();
    // to get metadata first
    Metadata metadata = null;

    metadata = excelParser.parseExcel();

    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {

      System.out.println(name + " : " + metadata.get(name));
      addMetadata(name, metadata.get(name));
    }

    if (workbook.getNumberOfSheets() > 0) {
      sheetFeatures = new HashMap<>();
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        //Sheet workSheet = workbook.getSheetAt(i);
        String sheetName = sheetParser(workbook, i, sheetFeatures);

        System.out.println("SheetNo: " + (i+1) + "sheetFeatures is " +
            sheetFeatures.get(sheetName).toString());
        //ExcelSheetFeature sheetFeature = new ExcelSheetFeature(mediaType);
        //sheetFeature.extract(workbook, i);
        //sheetFeatures.add(sheetFeature);

      }
    }

    workbook.close();

  }

  private String sheetParser(Workbook workbook, int sheetNo,
      Map<String, List<List<String>>> sheetFeatures) {

    String sheetName = "";
    if (workbook != null) {
      Sheet workSheet = workbook.getSheetAt(sheetNo);

      sheetName = workSheet.getSheetName();

      // iterate through list of records
      Iterator<Row> rowIterator = workSheet.rowIterator();
      int totalRow = 0;
      List<List<String>> contents = new ArrayList<>();
      HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
      HashMap<Integer, String> colMapper = new HashMap<Integer, String>();

      while (rowIterator.hasNext()) {
        Row row =  rowIterator.next();

        int colCount = 0;

        ArrayList<String> recordBuilder = new ArrayList<String>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
          Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
          String value = FeatureExtractorUtil.cellValue(cell);
          recordBuilder.add(value);

          if (totalRow == 0) {
            FeatureExtractorUtil.buildHeaderMapperAndContent(
                value, colRecords, colMapper, colCount);

          } else {
            FeatureExtractorUtil.buildContent(value, colRecords, colMapper, colCount);
          }
          colCount++;
        }
        contents.add(recordBuilder);
        totalRow++;

      }
      sheetFeatures.put(sheetName, contents);
      System.out.println("Col is :" + colRecords.toString());


    } else {
      System.out.println("Not able to parse");
    }
    return sheetName;
    // close readers
  }
}
