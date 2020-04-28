package innohack.gem.entity.rule.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.util.FileUtilForTesting;
import innohack.gem.example.tika.TikaUtil;
import innohack.gem.example.util.FileUtil;
import innohack.gem.filegen.CsvFileGenerator;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CsvHeaderColumnValueTest {

  @Test
  public void testCheckValidCSVHeader() throws Exception {

    CsvHeaderColumnValue csvHeader = new CsvHeaderColumnValue("Customer Name,Contact Number,Email");
    System.out.println("Testing testCSVContentParser");
    String path = "target/samples/csv/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    List<String> headers =
        Arrays.asList(
            "Customer ID", "Customer Name", "Gender", "Address", "Contact Number", "Email");


    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    CsvFileGenerator.generateFixedCustomerCsvFiles(1, Paths.get(path), 100000, filename);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      assertTrue(csvHeader.check(gFile));

    }

  }

  @Test
  public void testCheckInvalidCSVHeaderCustomerName() throws Exception {

    CsvHeaderColumnValue csvHeader = new CsvHeaderColumnValue("Customer Name,Contact Number,Email");
    System.out.println("Testing testCSVContentParser");
    String path = "target/samples/csv/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    List<String> headers =
        Arrays.asList(
            "Customer ID", "CustomerName", "Gender", "Address", "Contact Number", "Email");


    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    CsvFileGenerator.generateFixedCustomerCsvFilesWithHeader(1, Paths.get(path), 100000,
        filename, headers);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      // Test false because customer name is customername
      assertFalse(csvHeader.check(gFile));

    }

  }

  @Test
  public void testCheckInvalidCSVHeaderEmail() throws Exception {

    CsvHeaderColumnValue csvHeader = new CsvHeaderColumnValue("Customer Name,Contact Number,MailADDR");
    System.out.println("Testing testCSVContentParser");
    String path = "target/samples/csv/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    List<String> headers =
        Arrays.asList(
            "Customer ID", "Customer Name", "Gender", "Address", "Contact Number", "Email");


    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    File delFile = new File(path + filename);
    FileUtilForTesting.deleteTestFile(delFile);

    CsvFileGenerator.generateFixedCustomerCsvFilesWithHeader(1, Paths.get(path), 100000,
        filename, headers);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      // Test false because emailAADR
      assertFalse(csvHeader.check(gFile));

    }

  }
}
