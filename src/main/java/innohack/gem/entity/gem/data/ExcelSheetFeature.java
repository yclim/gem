package innohack.gem.entity.gem.data;

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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted excel data */
public class ExcelSheetFeature extends AbstractFeature {

  private List<String> header = new ArrayList<String>();
  private ArrayList<ArrayList<String>> contents = new ArrayList<ArrayList<String>>();
  private HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
  private HashMap<Integer, String> colMapper = new HashMap<Integer, String>();
  private int totalRow = 0;
  private TikaExcelParser excelParser;
  private String sheetTitle = "";

  public ExcelSheetFeature() {
    super(Target.EXCEL);
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
        HSSFRow row = (HSSFRow) rowIterator.next();

        if (totalRow == 0) {
          // this is for the header row
          int colCount = 0;

          for (int i = 0; i < row.getLastCellNum(); i++) {
            HSSFCell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            System.out.print(cell.toString() + " ");

            String value = cellValue(cell);
            header.add(value);

            if (!colRecords.containsKey(value)) {
              colRecords.put(value, new ArrayList<String>());
              colMapper.put(colCount, value);
              colCount++;
            }

            System.out.print(value + " ");
          }
          totalRow++;
        } else {

          // this is for the records row
          ArrayList<String> recordBuilder = new ArrayList<String>();
          int colCount = 0;

          for (int i = 0; i < row.getLastCellNum(); i++) {
            HSSFCell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            System.out.print(cell.toString() + " ");

            String value = cellValue(cell);
            recordBuilder.add(value);

            if (colMapper.containsKey(colCount)) {
              String colHeader = colMapper.get(colCount);

              if (colRecords.containsKey(colHeader)) {
                ArrayList<String> colCell = colRecords.get(colHeader);
                colCell.add(value);
                colRecords.replace(colHeader, colCell);
                System.out.print(value + " ");
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


  }

  private String cellValue(HSSFCell cell) {

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
}
