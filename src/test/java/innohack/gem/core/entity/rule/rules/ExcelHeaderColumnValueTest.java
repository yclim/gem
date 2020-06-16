package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import java.io.File;
import org.junit.jupiter.api.Test;

public class ExcelHeaderColumnValueTest {

  @Test
  public void testCheckMatchedExcelHeaderAndSheetName() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer", "CAR_ID,CAR_DEALER,PRICE");
    File excelFile = new File("src/test/resources/cars_0.xls");
    GEMFile gemFile = GEMMain.extractFeature(excelFile);
    assertTrue(excelHeader.check(gemFile));
  }

  @Test
  public void testCheckUnmatchedExcelHeader() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer", "CAR_NO,CAR_DEALER,PRICE");
    File excelFile = new File("src/test/resources/cars_0.xls");
    GEMFile gemFile = GEMMain.extractFeature(excelFile);
    assertFalse(excelHeader.check(gemFile));
  }

  @Test
  public void testCheckUnmatchedExcelSheetName() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer Error", "CAR_ID,CAR_DEALER,PRICE");
    File excelFile = new File("src/test/resources/cars_0.xls");
    GEMFile gemFile = GEMMain.extractFeature(excelFile);
    assertFalse(excelHeader.check(gemFile));
  }
}
