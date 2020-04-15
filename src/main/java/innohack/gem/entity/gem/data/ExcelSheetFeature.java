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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted excel data */
public class ExcelSheetFeature extends AbstractFeature {

  private List<List<String>> contents = new ArrayList<>();
  private HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
  private HashMap<Integer, String> colMapper = new HashMap<Integer, String>();
  private int totalRow = 0;
  private String sheetTitle = "";

  public ExcelSheetFeature(MediaType mediaType) {
    super(Target.EXCEL);
 ;
  }

  @Override
  public void extract(File f) {

  }

  public void extract(Workbook workbook, int sheetNo) {
    // TODO extraction method for EXCEL
    contentParser(workbook, sheetNo);
  }

  private void contentParser(Workbook workbook, int sheetNo) {

  if (workbook != null) {
      Sheet workSheet = workbook.getSheetAt(sheetNo);

      sheetTitle = workSheet.getSheetName();

      // iterate through list of records
      Iterator<Row> rowIterator = workSheet.rowIterator();

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
      System.out.println("Col is :" + colRecords.toString());

    } else {
      System.out.println("Not able to parse");
    }

    // close readers
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
