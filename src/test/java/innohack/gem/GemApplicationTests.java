package innohack.gem;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.example.tika.TikaUtil;
import innohack.gem.example.util.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import innohack.gem.filegen.CsvFileGenerator;
import innohack.gem.entity.feature.CsvFeature;
import org.springframework.util.Assert;

@SpringBootTest
class GemApplicationTests {

  private String CsvFeature = "innohack.gem.entity.feature.CsvFeature";
  private String ExcelFeature = "innohack.gem.entity.feature.ExcelFeature";
  private String TikaFeature = "innohack.gem.entity.feature.TikaFeature";


  @Test
  void contextLoads() {}

  @Test
  void testCSVParser() throws Exception {
    File file = new File("target/samples");
    file.mkdir();

    CsvFileGenerator.generateFixedCustomerCsvFiles(1, Paths.get("target/samples"),
        100000);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath("target/samples");

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      Collection<AbstractFeature> abstractFeatureC = gFile.getData();

      Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

      //contains both tika and csv feature
      assert(abstractFeatureC.size() == 2);
      while (iterator.hasNext()) {
        AbstractFeature abs = iterator.next();
        if (abs.getClass().getName().equals(CsvFeature)) {
          testCSVContents((CsvFeature)abs);
        }
        else if (abs.getClass().getName().equals(TikaFeature)) {
          testCSVTikaMetadata((TikaFeature)abs);
        }

        if (abs.getMetadata() != null) {
          abs.printMetadata();
        }
      }
    }
   }

   void testCSVTikaMetadata (TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;

    Map<String, String> metaData = tikaFeature.getMetadata();
    int metaDataCount = 0;
    for (Map.Entry<String, String> entry : metaData.entrySet()) {
      if (metaDataCount == 0) {
        assert(entry.getKey().equals("X-Parsed-By"));
        assert(entry.getValue().equals("org.apache.tika.parser.DefaultParser"));
      }
      else if (metaDataCount == 1) {
        assert(entry.getKey().equals("Content-Encoding"));
        assert(entry.getValue().equals("windows-1252"));
      }else if (metaDataCount == 2) {
        assert(entry.getKey().equals("csv:delimiter"));
        assert(entry.getValue().equals("comma"));
      }else if (metaDataCount == 3) {
        assert(entry.getKey().equals("Content-Type"));
        assert(entry.getValue().equals("text/csv; charset=windows-1252; delimiter=comma"));

      }
       metaDataCount++;
    }

   }

   void testCSVContents (CsvFeature abs) {
       CsvFeature csvFeature = (CsvFeature) abs;
       List<List<String>> dataTable = csvFeature.getTableData();
       int rowCount = 0;
       for (List<String> row : dataTable) {

         if (rowCount == 0) {
           for (int i = 0; i < row.size(); i++) {
             switch (i) {
               case 0: assert(row.get(i).equals("Customer ID"));
                 break;
               case 1: assert(row.get(i).equals("Customer Name"));
                 break;
               case 2: assert(row.get(i).equals("Gender"));
                 break;
               case 3: assert(row.get(i).equals("Address"));
                 break;
               case 4: assert(row.get(i).equals("Contact Number"));
                 break;
               case 5: assert(row.get(i).equals("Email"));
                 break;
               default:
                 break;
             }

           }

         }else {
           int custIdVariable = 100000;

           for (int i = 0; i < row.size(); i++) {

             // System.out.println("i is " + i + " value is " + row.get(i));
             int custIdInt = (rowCount-1) + custIdVariable;
             String custId = String.valueOf(custIdInt);
             String name = custId + "_" + "SampleName";
             String gender = "F";
             String emailSuffix = "@gmail.com";
             if ((rowCount-1) % 2 == 0) {
               gender = "M";
               emailSuffix = "@yahoo.com";
             }
             String address = custId + "_" + "SampleAddress";

             String contactNumber = custIdInt + 900000000 + "";
             String email = name + emailSuffix;

             switch (i) {
               case 0: assert(row.get(i).equals(custIdInt+""));
                 break;
               case 1: assert(row.get(i).equals(name));
                 break;
               case 2: assert(row.get(i).equals(gender));
                 break;
               case 3: assert(row.get(i).equals(address));
                 break;
               case 4: assert(row.get(i).equals(contactNumber));
                 break;
               case 5: assert(row.get(i).equals(email));
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
