package innohack.gem.entity.feature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.util.FileUtilForTesting;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TikaFeatureTests {

  private String TikaFeature = "innohack.gem.entity.feature.TikaFeature";

  @Test
  void TestTikaMetadataParser() throws Exception {
    System.out.println("Testing testTikaMetadataParser");
    String path = "src/test/resources";

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    // CsvFileGenerator.generateFixedCustomerCsvFiles(1, Paths.get(path), 100000, filename);

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    assertTrue(abstractFeatureC.size() == 2);
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(TikaFeature)) {
        TestMetadata((innohack.gem.entity.feature.TikaFeature) abs);
      }
    }
  }

  void TestMetadata(TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;

    String OS = System.getProperty("os.name").toLowerCase();

    System.out.println(OS);

    if (FileUtilForTesting.isWindows(OS)) {
      System.out.println("This is Windows");
    } else if (FileUtilForTesting.isMac(OS)) {
      System.out.println("This is Mac");
    } else if (FileUtilForTesting.isUnix(OS)) {
      System.out.println("This is Unix or Linux");
    } else if (FileUtilForTesting.isSolaris(OS)) {
      System.out.println("This is Solaris");
    } else {
      System.out.println("Your OS is not support!!");
    }

    Map<String, String> metaData = tikaFeature.getMetadata();
    int metaDataCount = 0;
    if (metaData != null) {
      for (Map.Entry<String, String> entry : metaData.entrySet()) {
        System.out.println("Key is " + entry.getKey() + " value is " + entry.getValue());
        if (metaDataCount == 0) {
          assertTrue(entry.getKey().equals("X-Parsed-By"));
          assertTrue(entry.getValue().equals("org.apache.tika.parser.DefaultParser"));
        } else if (metaDataCount == 1) {
          assertTrue(entry.getKey().equals("Content-Encoding"));
          if (FileUtilForTesting.isWindows(OS)) {
            assertTrue(entry.getValue().equals("windows-1252"));
            System.out.println("This is Windows");
          } else if (FileUtilForTesting.isMac(OS)) {
            assertFalse(entry.getValue().equals("windows-1252"));
            System.out.println("This is Mac");
          } else if (FileUtilForTesting.isUnix(OS)) {
            assertFalse(entry.getValue().equals("windows-1252"));
            System.out.println("This is Unix or Linux");
          } else if (FileUtilForTesting.isSolaris(OS)) {
            assertFalse(entry.getValue().equals("windows-1252"));
            System.out.println("This is Solaris");
          } else {
            assertFalse(entry.getValue().equals("windows-1252"));
            System.out.println("Your OS is not support!!");
          }

        } else if (metaDataCount == 2) {
          assertTrue(entry.getKey().equals("csv:delimiter"));
          assertTrue(entry.getValue().equals("comma"));
        } else if (metaDataCount == 3) {
          assertTrue(entry.getKey().equals("Content-Type"));
          // assertTrue (entry.getValue().equals("text/csv; charset=windows-1252;
          // delimiter=comma"));
        }
        metaDataCount++;
      }
    }
  }
}
