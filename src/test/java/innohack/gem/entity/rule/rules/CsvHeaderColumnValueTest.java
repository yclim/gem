package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import org.junit.jupiter.api.Test;

public class CsvHeaderColumnValueTest {

  @Test
  public void testCheckValidCSVHeader() throws Exception {

    // changes for this
    CsvHeaderColumnValue csvHeader = new CsvHeaderColumnValue("Customer Name,Contact Number,Email");
    System.out.println("Testing testCSVContentParser");
    String path = "src/test/resources";

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    assertTrue(csvHeader.check(gFile));
  }

  @Test
  public void testCheckInvalidCSVHeaderCustomerName() throws Exception {

    CsvHeaderColumnValue csvHeader =
        new CsvHeaderColumnValue("Customer Name,Contactor Number,Email");
    System.out.println("Testing testCSVContentParser");
    String path = "src/test/resources";

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    // Test false because customer name is customername
    assertFalse(csvHeader.check(gFile));
  }

  @Test
  public void testCheckInvalidCSVHeaderEmail() throws Exception {

    CsvHeaderColumnValue csvHeader =
        new CsvHeaderColumnValue("Customer Name,Contact Number,MailADDR");
    System.out.println("Testing testCSVContentParser");
    String path = "src/test/resources";

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    // Test false because emailAADR
    assertFalse(csvHeader.check(gFile));
  }
}
