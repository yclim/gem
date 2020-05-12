package innohack.gem.entity.feature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TikaFeatureTests {

  @Test
  void testTikaMetadataParser() throws Exception {
    String path = "src/test/resources";
    String filename = "customer_0.csv";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    assertTrue(abstractFeatureC.size() == 2);
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(TikaFeature.class.getName())) {
        testMetadata((innohack.gem.entity.feature.TikaFeature) abs);
      }
    }
  }

  private void testMetadata(TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;

    String OS = System.getProperty("os.name").toLowerCase();

    //    if (FileUtilForTesting.isWindows(OS)) {
    //      System.out.println("This is Windows");
    //    } else if (FileUtilForTesting.isMac(OS)) {
    //      System.out.println("This is Mac");
    //    } else if (FileUtilForTesting.isUnix(OS)) {
    //      System.out.println("This is Unix or Linux");
    //    } else if (FileUtilForTesting.isSolaris(OS)) {
    //      System.out.println("This is Solaris");
    //    } else {
    //      System.out.println("Your OS is not support!!");
    //    }

    Map<String, String> metaData = tikaFeature.getMetadata();
    int metaDataCount = 0;
    if (metaData != null) {
      for (Map.Entry<String, String> entry : metaData.entrySet()) {

        if (metaDataCount == 0) {
          assertTrue(entry.getKey().equals("X-Parsed-By"));
          assertTrue(entry.getValue().equals("org.apache.tika.parser.DefaultParser"));
        } else if (metaDataCount == 1) {
          assertTrue(entry.getKey().equals("Content-Encoding"));
          if (FileUtilForTesting.isWindows(OS)) {
            assertTrue(entry.getValue().equals("ISO-8859-1"));
            //            System.out.println("This is Windows");
          } else if (FileUtilForTesting.isMac(OS)) {
            assertFalse(entry.getValue().equals("windows-1252"));
            //            System.out.println("This is Mac");
          } else if (FileUtilForTesting.isUnix(OS)) {
            // XXX this unit test is very odd... tests for negation is not very conclusive...
            assertFalse(entry.getValue().equals("windows-1252"));
            //            System.out.println("This is Unix or Linux");
          } else if (FileUtilForTesting.isSolaris(OS)) {
            assertFalse(entry.getValue().equals("windows-1252"));
            //            System.out.println("This is Solaris");
          } else {
            assertFalse(entry.getValue().equals("windows-1252"));
            //            System.out.println("Your OS is not support!!");
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

  private static class FileUtilForTesting {

    public static boolean isWindows(String OS) {

      return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac(String OS) {

      return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix(String OS) {

      return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    public static boolean isSolaris(String OS) {

      return (OS.indexOf("sunos") >= 0);
    }
  }
}
