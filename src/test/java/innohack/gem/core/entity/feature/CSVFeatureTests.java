package innohack.gem.entity.feature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CSVFeatureTests {

  @Test
  void testCSVContentParser() throws Exception {
    String path = "src/test/resources";

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    assertTrue(abstractFeatureC.size() == 2);
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(CsvFeature.class.getName())) {
        testCSVContents((CsvFeature) abs);
      }
    }
  }

  void testCSVContents(CsvFeature abs) {
    CsvFeature csvFeature = abs;
    List<List<String>> dataTable = csvFeature.getTableData();
    int rowCount = 0;
    for (List<String> row : dataTable) {

      if (rowCount == 0) {
        for (int i = 0; i < row.size(); i++) {
          switch (i) {
            case 0:
              assertTrue(row.get(i).equals("Customer ID"));
              break;
            case 1:
              assertTrue(row.get(i).equals("Customer Name"));
              break;
            case 2:
              assertTrue(row.get(i).equals("Gender"));
              break;
            case 3:
              assertTrue(row.get(i).equals("Address"));
              break;
            case 4:
              assertTrue(row.get(i).equals("Contact Number"));
              break;
            case 5:
              assertTrue(row.get(i).equals("Email"));
              break;
            default:
              break;
          }
        }

      } else {
        int custIdVariable = 100000;

        for (int i = 0; i < row.size(); i++) {

          // System.out.println("i is " + i + " value is " + row.get(i));
          int custIdInt = (rowCount - 1) + custIdVariable;
          String custId = String.valueOf(custIdInt);
          String name = custId + "_" + "SampleName";
          String gender = "F";
          String emailSuffix = "@gmail.com";
          if ((rowCount - 1) % 2 == 0) {
            gender = "M";
            emailSuffix = "@yahoo.com";
          }
          String address = custId + "_" + "SampleAddress";

          String contactNumber = custIdInt + 900000000 + "";
          String email = name + emailSuffix;

          switch (i) {
            case 0:
              assertTrue(row.get(i).equals(custIdInt + ""));
              break;
            case 1:
              assertTrue(row.get(i).equals(name));
              break;
            case 2:
              assertTrue(row.get(i).equals(gender));
              break;
            case 3:
              assertTrue(row.get(i).equals(address));
              break;
            case 4:
              assertTrue(row.get(i).equals(contactNumber));
              break;
            case 5:
              assertTrue(row.get(i).equals(email));
              break;
            default:
              break;
          }
        }
      }
      rowCount++;
    }
  }

  static final String TEST_FILE_DIRECTORY = "src/test/resources/csvfeature";

  static CsvFeature getCsvFeature(String filename) {
    GEMFile gemFile = new GEMFile(filename, TEST_FILE_DIRECTORY).extract();
    Optional<CsvFeature> csvFeatureOpt =
        gemFile.getData().stream()
            .filter(f -> f instanceof CsvFeature)
            .map(f -> (CsvFeature) f)
            .findAny();
    assertTrue(csvFeatureOpt.isPresent());
    return csvFeatureOpt.get();
  }

  @Test
  void testOneColumnCsv() {
    CsvFeature feature = getCsvFeature("one-column.csv");
    assertEquals(4, feature.getTableData().size(), "number of row match");
    assertEquals(1, feature.getTableData().get(0).size(), "number of column match");
    assertEquals("c1", feature.getTableData().get(0).get(0), "first row value match");
    assertEquals("c1", feature.getHeaders().get(0), "first header value match");
    assertEquals("f", feature.getTableData().get(1).get(2), "last data value match");
  }

  @Test
  void testSemiColonSeperated() {
    CsvFeature feature = getCsvFeature("semi-colon-seperated.csv");
    assertEquals(3, feature.getTableData().size(), "number of row match");
    assertEquals(3, feature.getTableData().get(0).size(), "number of column match");
    assertEquals(
        Arrays.asList("c1", "c2", "c3"), feature.getTableData().get(0), "first row value match");
    assertEquals(Arrays.asList("c1", "c2", "c3"), feature.getHeaders(), "header value match");
    assertEquals("a", feature.getTableData().get(1).get(0), "first data value match");
    assertEquals("f", feature.getTableData().get(2).get(2), "last data value match");
  }

  @Test
  void testTabSeperated() {
    CsvFeature feature = getCsvFeature("tab-seperated.csv");
    assertEquals(3, feature.getTableData().size(), "number of row match");
    assertEquals(3, feature.getTableData().get(0).size(), "number of column match");
    assertEquals(
        Arrays.asList("c1", "c2", "c3"), feature.getTableData().get(0), "first row value match");
    assertEquals(Arrays.asList("c1", "c2", "c3"), feature.getHeaders(), "header value match");
    assertEquals("a", feature.getTableData().get(1).get(0), "first data value match");
    assertEquals("f", feature.getTableData().get(2).get(2), "last data value match");
  }

  @Test
  void testSpaceSeperated() {
    CsvFeature feature = getCsvFeature("space-seperated.csv");
    assertEquals(3, feature.getTableData().size(), "number of row match");
    assertEquals(1, feature.getTableData().get(0).size(), "number of column match");
    assertEquals(Arrays.asList("c1 c2 c3"), feature.getTableData().get(0), "first row value match");
    assertEquals(Arrays.asList("c1 c2 c3"), feature.getHeaders(), "header value match");
    assertEquals("a b c", feature.getTableData().get(1).get(0), "first data value match");
  }
}
