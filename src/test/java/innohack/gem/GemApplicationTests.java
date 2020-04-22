package innohack.gem;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.example.tika.TikaUtil;
import innohack.gem.example.util.FileUtil;
import innohack.gem.filegen.GenUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import innohack.gem.filegen.CsvFileGenerator;
import innohack.gem.filegen.ExcelFileGenerator;
import innohack.gem.entity.feature.CsvFeature;
import innohack.gem.entity.feature.ExcelFeature;

import org.springframework.util.Assert;
import org.apache.commons.io.FileUtils;

@SpringBootTest
class GemApplicationTests {

  private String CsvFeature = "innohack.gem.entity.feature.CsvFeature";
  private String ExcelFeature = "innohack.gem.entity.feature.ExcelFeature";
  private String TikaFeature = "innohack.gem.entity.feature.TikaFeature";

  static final String[] carBrands =
      ("Seat Renault Peugeot Dacia Citroën Opel Alfa Romeo Škoda Chevrolet Porsche Honda"
          + " Subaru Mazda Mitsubishi Lexus Toyota BMW Volkswagen Suzuki Mercedes-Benz Saab Audi Kia Land Rover "
          + "Dodge Chrysler Ford Hummer Hyundai Infiniti Jaguar Jeep Nissan Volvo Daewoo Fiat MINI Rover Smart")
          .split("\\s+");

  static final String[] carModels =
      ("Alhambra Altea Arosa Cordoba Exeo Ibiza Leon Inca Toledo Captur "
          + "Clio Espace Express Fluence Kadjar Kangoo Koleos Laguna Latitude Mascott Mégane Scénic Talisman "
          + "Thalia Twingo Wind Bipper Dokker Duster Lodgy Logan Sandero Solenza Berlingo C-Crosser C-Elissée "
          + "C-Zero Evasion Jumper Jumpy Saxo Nemo Xantia Xsara Agila Ampera Antara Astra Calibra Campo Cascada "
          + "Corsa Frontera Insignia Kadett Meriva Mokka Movano Omega Signum Vectra Vivaro Zafira Favorit "
          + "Felicia Citigo Fabia Octavia Roomster Yeti Rapid Superb Alero Aveo Camaro Captiva Corvette "
          + "Cruze Epica Equinox Evanda Kalos Lacetti Lumina Malibu Matiz Nubira Orlando Spark Suburban "
          + "Tacuma Tahoe Trax Boxster Cayenne Cayman Macan Panamera Accord City Civic CR-V CR-X CR-Z "
          + "FR-V HR-V Insight Integra Jazz Legend Prelude Forester Impreza Justy Legacy Levorg "
          + "Outback Tribeca B-Fighter B2500 CX-3 CX-5 CX-7 CX-9 Demio MX-3 MX-5 MX-6 Premacy RX-7 RX-8 Carisma "
          + "Colt Eclipse Galant Grandis L200 L300 Lancer Outlander Pajero IS-F 4-Runner Auris Avensis Aygo "
          + "Camry Carina Celica Corolla GT86 Hiace Highlander Hilux Paseo Picnic Prius RAV4 "
          + "Sequoia Starlet Supra Tundra Verso Yaris Amarok Beetle Bora Caddy Life California"
          + " Caravelle Crafter CrossTouran Golf Jetta Lupo Multivan Passat Phaeton Polo Scirocco "
          + "Sharan Tiguan Touareg Touran Alto Baleno Ignis Jimny Kizashi Liana Samurai Splash Swift"
          + " Vitara Citan Sprinter S6/RS6 Avella Besta Carens Carnival Cee`d Cerato Magentis Opirus "
          + "Optima Picanto Pregio Pride Sephia Shuma Sorento Soul Sportage Venga Avenger Caliber Challenger"
          + " Charger Journey Magnum Nitro Stealth Viper Crossfire Neon Pacifica Plymouth Sebring Stratus "
          + "Voyager Aerostar B-Max C-Max Cortina Cougar Edge Escort Explorer F-150 F-250 Fiesta Focus Fusion"
          + " Galaxy Kuga Maverick Mondeo Mustang Orion Puma Ranger S-Max Sierra Transit Transit Windstar Accent"
          + " Atos Coupé Elantra Galloper Genesis Getz Grandeur H200 ix20 ix35 ix55 Lantra Matrix Sonata Terracan "
          + "Trajet Tucson Veloster Daimler F-Pace F-Type S-Type Sovereign X-Type XJ12 Cherokee Commander Compass "
          + "Patriot Renegade Wrangler Almera e-NV200 GT-R Insterstar Juke Leaf Maxima Micra Murano Navara Note "
          + "NV200 NV400 Pathfinder Patrol Pickup Pixo Primastar Primera Pulsar Qashqai Serena Sunny Terrano Tiida"
          + " Trade X-Trail XC60 XC70 XC90 Espero Kalos Lacetti Lanos Leganza Lublin Matiz Nexia Nubira Racer Tacuma"
          + " Tico 500L 500X Barchetta Brava Cinquecento Coupé Croma Doblo Ducato Florino Freemont Idea Linea Marea "
          + "Multipla Panda Punto Qubo Scudo Sedici Seicento Stilo Strada Talento Tipo Ulysse X1/9 Cooper Countryman "
          + "Cabrio City-Coupé Forfour Roadster")
          .split("\\s+");


  @Test
  void TestCSVContentParser() throws Exception {

    System.out.println("Testing testCSVContentParser");
    String path = "target/samples/csv/";
    File file = new File(path);
    file.mkdirs();


    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    CsvFileGenerator.generateFixedCustomerCsvFiles(1, Paths.get(path),
        100000, filename);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

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
          TestCSVContents((CsvFeature)abs);
        }

      }
    }
    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    FileUtils.forceDelete(new File(path + filename));
   }

  void TestCSVContents (CsvFeature abs) {
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

  @Test
  void TestTikaMetadataParser () throws Exception {
    System.out.println("Testing testTikaMetadataParser");
    String path = "target/samples/metadata/";
    File file = new File(path);
    file.mkdirs();

    String filenamePrefix = "customer_";
    String filename = filenamePrefix + 0 + ".csv";

    CsvFileGenerator.generateFixedCustomerCsvFiles(1, Paths.get(path),
        100000, filename);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

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
        if (abs.getClass().getName().equals(TikaFeature)) {
          TestMetadata((TikaFeature)abs);
        }

      }
    }
    // to delete after every use case
    System.out.println("Deleting " + path + filename);
    FileUtils.forceDelete(new File(path + filename));
  }


  void TestMetadata (TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;

    Map<String, String> metaData = tikaFeature.getMetadata();
    int metaDataCount = 0;
    if (metaData != null) {
      for (Map.Entry<String, String> entry : metaData.entrySet()) {
        System.out.println("Key is " + entry.getKey() + " value is " + entry.getValue());
        if (metaDataCount == 0) {
          assert (entry.getKey().equals("X-Parsed-By"));
          assert (entry.getValue().equals("org.apache.tika.parser.DefaultParser"));
        } else if (metaDataCount == 1) {
          assert (entry.getKey().equals("Content-Encoding"));
          // assert (entry.getValue().equals("windows-1252"));
        } else if (metaDataCount == 2) {
          assert (entry.getKey().equals("csv:delimiter"));
          assert (entry.getValue().equals("comma"));
        } else if (metaDataCount == 3) {
          assert (entry.getKey().equals("Content-Type"));
          //assert (entry.getValue().equals("text/csv; charset=windows-1252; delimiter=comma"));
        }
        metaDataCount++;
      }
    }
   }

  @Test
  void TestOneSheetExcelXlsContentParser() throws Exception {

    System.out.println("Testing testExcelXlsContentParser");
    String path = "target/samples/excelSheetOne/";

    String filenamePrefix = "cars_";
    String filename = filenamePrefix + 0 + ".xls";


    File file = new File(path);
    file.mkdirs();


    ExcelFileGenerator.generateFixedCarsExcelFiles(false,100, 1,
        Paths.get(path), filenamePrefix);

    TikaUtil tikaUtil = new TikaUtil();
    List<Path> results = FileUtil.walkPath(path);

    for (Path result : results) {
      System.out.println("each result is " + result.toAbsolutePath());

      GEMFile gFile = new GEMFile(result.getFileName().toString(), result.getParent().toString());
      gFile.extract();
      Collection<AbstractFeature> abstractFeatureC = gFile.getData();

      Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

      // contains both tika and excel feature
      assert (abstractFeatureC.size() == 2); // to delete after every use case
      while (iterator.hasNext()) {
        AbstractFeature abs = iterator.next();
        if (abs.getClass().getName().equals(ExcelFeature)) {
          TestOneSheetExcelContent((ExcelFeature) abs);
        }
      }
    }
    System.out.println("Deleting " + path + filename);

  }

  void TestOneSheetExcelContent(ExcelFeature abs) {
    ExcelFeature excelFeature = (ExcelFeature) abs;
    Map<String, List<List<String>>> dataTable = excelFeature.getSheetTableData();

    assert(dataTable.size() == 1);
    for (Entry<String, List<List<String>>> entry : dataTable.entrySet()) {
      System.out.println("entry: " + entry.getKey());
      assert(entry.getKey().equals("Cars"));
      int rowCount = 0;

      for (List<String> row : entry.getValue()) {

        if (rowCount == 0) {
          for (int i = 0; i < row.size(); i++) {
            switch (i) {
              case 0: assert(row.get(i).equals("CAR_ID"));
                break;
              case 1: assert(row.get(i).equals("CAR_BRAND"));
                break;
              case 2: assert(row.get(i).equals("CAR_MODEL"));
                break;
              case 3: assert(row.get(i).equals("YEAR"));
                break;
              case 4: assert(row.get(i).equals("CAPACITY"));
                break;
              default:
                break;
            }

          }

        }else {
         for (int i = 0; i < row.size(); i++) {
            int carVar = rowCount - 1;
            String id = String.valueOf(carVar);

            String brand = carBrands[carVar % carBrands.length];
            String model = carModels[carVar % carModels.length];
            String year = (1970 + carVar) + "";
            String capacity = (carVar % 8) + "";

            switch (i) {
              case 0: assert(row.get(i).equals(id+""));
                break;
              case 1: assert(row.get(i).equals(brand));
                break;
              case 2: assert(row.get(i).equals(model));
                break;
              case 3: assert(row.get(i).equals(year));
                break;
              case 4: assert(row.get(i).equals(capacity));
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
}
