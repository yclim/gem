package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class ExcelHeaderColumnValueTest {

  @Test
  public void testCheckMatchedExcelHeaderAndSheetName() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer", "CAR_ID,CAR_DEALER,PRICE");
    GEMFile excelFile = new GEMFile("cars_0.xls", "src/test/resources");
    excelFile.extract();
    assertTrue(excelHeader.check(excelFile));
  }

  @Test
  public void testCheckUnmatchedExcelHeader() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer", "CAR_NO,CAR_DEALER,PRICE");
    GEMFile excelFile = new GEMFile("cars_0.xls", "src/test/resources");
    excelFile.extract();
    assertFalse(excelHeader.check(excelFile));
  }

  @Test
  public void testCheckUnmatchedExcelSheetName() throws Exception {
    ExcelHeaderColumnValue excelHeader =
        new ExcelHeaderColumnValue("Cars Dealer Error", "CAR_ID,CAR_DEALER,PRICE");
    GEMFile excelFile = new GEMFile("cars_0.xls", "src/test/resources");
    excelFile.extract();
    assertFalse(excelHeader.check(excelFile));
  }
}
