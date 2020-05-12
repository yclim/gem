package innohack.gem.entity.feature;

import static org.junit.jupiter.api.Assertions.assertTrue;

import innohack.gem.entity.GEMFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;

class ExcelFeatureTests {

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
  void TestOneSheetExcelXlsContentParser() throws Exception {
    String path = "src/test/resources";

    String filenamePrefix = "manual_cars_";
    String filename = filenamePrefix + 0 + ".xls";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and excel feature
    assertTrue(abstractFeatureC.size() == 2); // to delete after every use case
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(ExcelFeature.class.getName())) {
        ExcelFeature excelFeature = (ExcelFeature) abs;
        Map<String, List<List<String>>> dataTable = excelFeature.getSheetTableData();
        assertTrue(dataTable.size() == 1);
        TestExcelContent(dataTable);
      }
    }
  }

  @Test
  void TestTwoSheetExcelXlsContentParser() throws Exception {
    String path = "src/test/resources";

    String filenamePrefix = "manual2_cars_";
    String filename = filenamePrefix + 0 + ".xls";

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and excel feature
    assertTrue(abstractFeatureC.size() == 2); // to delete after every use case
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(ExcelFeature.class.getName())) {
        ExcelFeature excelFeature = (ExcelFeature) abs;
        Map<String, List<List<String>>> dataTable = excelFeature.getSheetTableData();
        assertTrue(dataTable.size() == 2);
        TestExcelContent(dataTable);
      }
    }

    System.out.println("Deleting " + path + filename);
  }

  void TestExcelContent(Map<String, List<List<String>>> dataTable) {

    for (Entry<String, List<List<String>>> entry : dataTable.entrySet()) {
      List<List<String>> rows = entry.getValue();

      if (entry.getKey().equals("Cars")) {
        assertTrue(entry.getKey().equals("Cars"));
        TestOneSheetExcelContent(rows);

      } else if (entry.getKey().equals("Cars Dealer")) {
        assertTrue(entry.getKey().equals("Cars Dealer"));
        TestTwoSheetExcelContent(rows);
      }
    }
  }

  void TestOneSheetExcelContent(List<List<String>> rows) {
    int rowCount = 0;

    for (List<String> row : rows) {

      if (rowCount == 0) {
        for (int i = 0; i < row.size(); i++) {
          switch (i) {
            case 0:
              assertTrue(row.get(i).equals("CAR_ID"));
              break;
            case 1:
              assertTrue(row.get(i).equals("CAR_BRAND"));
              break;
            case 2:
              assertTrue(row.get(i).equals("CAR_MODEL"));
              break;
            case 3:
              assertTrue(row.get(i).equals("YEAR"));
              break;
            case 4:
              assertTrue(row.get(i).equals("CAPACITY"));
              break;
            default:
              break;
          }
        }

      } else {
        for (int i = 0; i < row.size(); i++) {
          int carVar = rowCount - 1;
          String id = String.valueOf(carVar);

          String brand = carBrands[carVar % carBrands.length];
          String model = carModels[carVar % carModels.length];
          String year = (1970 + carVar) + "";
          String capacity = (carVar % 8) + "";

          switch (i) {
            case 0:
              assertTrue(row.get(i).equals(id + ""));
              break;
            case 1:
              assertTrue(row.get(i).equals(brand));
              break;
            case 2:
              assertTrue(row.get(i).equals(model));
              break;
            case 3:
              assertTrue(row.get(i).equals(year));
              break;
            case 4:
              assertTrue(row.get(i).equals(capacity));
              break;
            default:
              break;
          }
        }
      }
      rowCount++;
    }
  }

  void TestTwoSheetExcelContent(List<List<String>> rows) {
    int rowCount = 0;

    for (List<String> row : rows) {

      if (rowCount == 0) {
        for (int i = 0; i < row.size(); i++) {
          switch (i) {
            case 0:
              assertTrue(row.get(i).equals("CAR_ID"));
              break;
            case 1:
              assertTrue(row.get(i).equals("CAR_DEALER"));
              break;
            case 2:
              assertTrue(row.get(i).equals("PRICE"));
              break;

            default:
              break;
          }
        }

      } else {
        for (int i = 0; i < row.size(); i++) {
          int carVar = rowCount - 1;
          String[] carDealerArray =
              ("Huat Huat Car, Best Auto, Fast Car Ptd Ltd, Dealer X, Premium Auto, "
                      + "Luxury Automobile, Prestige Auto")
                  .split(",");

          String carDealer = carDealerArray[carVar % carDealerArray.length];
          String price = String.valueOf((carVar * 1000) + 1000000);

          switch (i) {
            case 0:
              assertTrue(row.get(i).equals(carVar + ""));
              break;
            case 1:
              assertTrue(row.get(i).equals(carDealer));
              break;
            case 2:
              assertTrue(row.get(i).equals(price));
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
  void TestOneSheetExcelXlsxContentParser() throws Exception {
    String path = "src/test/resources";

    String filenamePrefix = "manual_cars_";
    String filename = filenamePrefix + 0 + ".xlsx";

    // ExcelFileGenerator.generateFixedCarsExcelFiles(
    //    XLSX, false, 100, 1, Paths.get(path), filenamePrefix);

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and excel feature
    assertTrue(abstractFeatureC.size() == 2); // to delete after every use case
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(ExcelFeature.class.getName())) {
        ExcelFeature excelFeature = (ExcelFeature) abs;
        Map<String, List<List<String>>> dataTable = excelFeature.getSheetTableData();
        assertTrue(dataTable.size() == 1);
        TestExcelContent(dataTable);
      }
    }
  }

  @Test
  void TestTwoSheetExcelXlsxContentParser() throws Exception {
    String path = "src/test/resources";

    String filenamePrefix = "manual2_cars_";
    String filename = filenamePrefix + 0 + ".xlsx";

    // ExcelFileGenerator.generateFixedCarsExcelFiles(
    //    XLSX, true, 100, 1, Paths.get(path), filenamePrefix);

    GEMFile gFile = new GEMFile(filename, path);
    gFile.extract();
    Collection<AbstractFeature> abstractFeatureC = gFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and excel feature
    assertTrue(abstractFeatureC.size() == 2); // to delete after every use case
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(ExcelFeature.class.getName())) {
        ExcelFeature excelFeature = (ExcelFeature) abs;
        Map<String, List<List<String>>> dataTable = excelFeature.getSheetTableData();
        assertTrue(dataTable.size() == 2);
        TestExcelContent(dataTable);
      }
    }
  }
}
