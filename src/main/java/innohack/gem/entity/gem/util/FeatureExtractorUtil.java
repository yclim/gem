package innohack.gem.entity.gem.util;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class FeatureExtractorUtil {

  public static void buildHeaderMapperAndContent(String cell,
      HashMap<String, ArrayList<String>> colRecords,
      HashMap<Integer, String> colMapper, int colCount) {


    if (!colRecords.containsKey(cell)) {
      colRecords.put(cell, new ArrayList<String>());
      colMapper.put(colCount, cell);

    }

  }

  public static void  buildContent(String cell,
      HashMap<String, ArrayList<String>> colRecords,
      HashMap<Integer, String> colMapper, int colCount) {

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
}
