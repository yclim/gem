package innohack.gem.entity.feature;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

class CSVFeatureTests {

  @Test
  void TestCSVContentParser() throws Exception {
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
        TestCSVContents((CsvFeature) abs);
      }
    }
  }

  void TestCSVContents(CsvFeature abs) {
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
}
