package innohack.gem.core.feature;

import java.io.File;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Object to hold wrap extracted excel data */
public class ExcelFeature extends AbstractFeature {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFeature.class);
  private Map<String, List<List<String>>> sheetTableData;

  public void setSheetTableData(Map<String, List<List<String>>> sheetTableData) {
    this.sheetTableData = sheetTableData;
  }

  public Map<String, List<List<String>>> getSheetTableData() {
    return sheetTableData;
  }

  @Override
  public void extract(File f) throws Exception {

    File file = new File(String.valueOf(f.toPath().toAbsolutePath()));
    try (Workbook workbook = WorkbookFactory.create(file, null, true)) {

      if (workbook.getNumberOfSheets() > 0) {
        sheetTableData = new HashMap<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
          String sheetName = sheetParser(workbook, i, sheetTableData);
          List<List<String>> dataList = sheetTableData.get(sheetName);

          LOGGER.debug("size of row" + dataList.size());
          LOGGER.debug(
              "SheetNo: "
                  + (i + 1)
                  + "sheetFeatures is "
                  + sheetTableData.get(sheetName).toString());
        }
      }
    }
  }

  private String sheetParser(
      Workbook workbook, int sheetNo, Map<String, List<List<String>>> tableData) {

    String sheetName = "";
    Sheet workSheet = workbook.getSheetAt(sheetNo);
    sheetName = workSheet.getSheetName();

    // iterate through list of records
    Iterator<Row> rowIterator = workSheet.rowIterator();
    List<List<String>> contents = new ArrayList<>();

    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      ArrayList<String> recordBuilder = new ArrayList<String>();
      for (int i = 0; i < row.getLastCellNum(); i++) {
        Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        String value = cellValue(cell);
        recordBuilder.add(value);
      }
      contents.add(recordBuilder);
    }
    tableData.put(sheetName, contents);
    return sheetName;
  }

  private String cellValue(Cell cell) {

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
