package innohack.gem.core.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.core.GEMMain;
import innohack.gem.core.entity.GEMFile;
import java.io.File;
import org.junit.jupiter.api.Test;

public class CsvHeaderColumnValueTest {

  @Test
  public void testCheckValidCSVHeader() throws Exception {

    // changes for this
    CsvHeaderColumnValue csvHeader = new CsvHeaderColumnValue("Customer Name,Contact Number,Email");
    String path = "src/test/resources";
    String filename = "customer_0.csv";

    File file = new File(path + "/" + filename);
    GEMFile gFile = GEMMain.extractFeature(file);
    assertTrue(csvHeader.check(gFile));
  }

  @Test
  public void testCheckInvalidCSVHeaderCustomerName() throws Exception {

    CsvHeaderColumnValue csvHeader =
        new CsvHeaderColumnValue("Customer Name,Contactor Number,Email");
    String path = "src/test/resources";
    String filename = "customer_0.csv";

    File file = new File(path + "/" + filename);
    GEMFile gFile = GEMMain.extractFeature(file);
    // Test false because customer name is customername
    assertFalse(csvHeader.check(gFile));
  }

  @Test
  public void testCheckInvalidCSVHeaderEmail() throws Exception {

    CsvHeaderColumnValue csvHeader =
        new CsvHeaderColumnValue("Customer Name,Contact Number,MailADDR");
    String path = "src/test/resources";
    String filename = "customer_0.csv";

    File file = new File(path + "/" + filename);
    GEMFile gFile = GEMMain.extractFeature(file);
    // Test false because emailAADR
    assertFalse(csvHeader.check(gFile));
  }
}
