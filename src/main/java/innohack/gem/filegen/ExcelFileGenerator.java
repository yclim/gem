package innohack.gem.filegen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Code to generate Excel files with two sheets - first sheet contains car data that looks like:
 *
 * <p>CAR_ID CAR_BRAND CAR_MODEL YEAR CAPACITY 0 Rover Trade 1980 2.7
 *
 * <p>Second sheet contains car dealer data that looks like:
 *
 * <p>CAR_ID CAR_DEALER PRICE 0 Best Auto 108000
 */
public class ExcelFileGenerator {

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

  public static void generateCarsExcelFiles(int numOfFiles, Path dest) throws IOException {
    int numOfLines = GenUtil.randomInt(300);
    String filenamePrefix = "cars_";
    for (int i = 0; i < numOfFiles; i++) {
      List<List<String>> sheetOneTable = generateSheetOneDataTables(numOfLines);
      List<List<String>> sheetTwoTable = generateSheetTwoDataTables(numOfLines);
      Workbook workbook = new HSSFWorkbook();
      populateSheet(workbook, "Cars", sheetOneTable);
      populateSheet(workbook, "Cars Dealer", sheetTwoTable);
      String filename = filenamePrefix + i + ".xls";

      FileOutputStream outputStream =
          new FileOutputStream(Paths.get(dest.toString(), filename).toFile());
      workbook.write(outputStream);
      workbook.close();
      outputStream.close();
    }
  }

  public static void generateFixedCarsExcelFiles(
      String excelType,
      boolean multiSheets,
      int numLines,
      int numOfFiles,
      Path dest,
      String fileNamePrefix)
      throws IOException {
    int numOfLines = numLines;

    for (int i = 0; i < numOfFiles; i++) {
      Workbook workbook;
      String extension = ".xls";

      if (excelType.equals("xls")) {
        workbook = new HSSFWorkbook();
      } else {
        workbook = new XSSFWorkbook();
        extension = ".xlsx";
      }
      List<List<String>> sheetOneTable = generateFixedSheetOneDataTables(numOfLines);
      populateSheet(workbook, "Cars", sheetOneTable);

      if (multiSheets) {
        List<List<String>> sheetTwoTable = generateFixedSheetTwoDataTables(numOfLines);
        populateSheet(workbook, "Cars Dealer", sheetTwoTable);
      }
      String filename = fileNamePrefix + i + extension;

      FileOutputStream outputStream =
          new FileOutputStream(Paths.get(dest.toString(), filename).toFile());
      workbook.write(outputStream);

      workbook.close();
      outputStream.close();
    }
  }

  public static List<List<String>> generateSheetOneDataTables(int rows) {
    List<List<String>> table = new ArrayList<>();
    List<String> headers = Arrays.asList("CAR_ID", "CAR_BRAND", "CAR_MODEL", "YEAR", "CAPACITY");
    table.add(headers);
    for (int i = 0; i < rows; i++) {
      String id = String.valueOf(i);
      String brand = carBrands[GenUtil.randomInt(carBrands.length)];
      String model = carModels[GenUtil.randomInt(carModels.length)];
      String year = String.valueOf(GenUtil.randamIntRange(1980, 2020));
      String capacity = String.valueOf((double) GenUtil.randamIntRange(10, 40) / 10.0);
      table.add(Arrays.asList(id, brand, model, year, capacity));
    }
    return table;
  }

  public static List<List<String>> generateFixedSheetOneDataTables(int rows) {
    List<List<String>> table = new ArrayList<>();
    List<String> headers = Arrays.asList("CAR_ID", "CAR_BRAND", "CAR_MODEL", "YEAR", "CAPACITY");
    table.add(headers);
    for (int i = 0; i < rows; i++) {
      String id = String.valueOf(i);

      String brand = carBrands[i % carBrands.length];
      String model = carModels[i % carModels.length];
      String year = (1970 + i) + "";
      String capacity = (i % 8) + "";
      table.add(Arrays.asList(id, brand, model, year, capacity));
    }
    return table;
  }

  public static List<List<String>> generateFixedSheetTwoDataTables(int rows) {
    List<List<String>> table = new ArrayList<>();
    List<String> headers = Arrays.asList("CAR_ID", "CAR_DEALER", "PRICE");
    table.add(headers);
    for (int i = 0; i < rows; i++) {
      String id = String.valueOf(i);
      String[] carDealerArray =
          ("Huat Huat Car, Best Auto, Fast Car Ptd Ltd, Dealer X, Premium Auto, "
                  + "Luxury Automobile, Prestige Auto")
              .split(",");

      String carDealer = carDealerArray[i % carDealerArray.length];
      String price = String.valueOf((i * 1000) + 1000000);
      table.add(Arrays.asList(id, carDealer, price));
    }
    return table;
  }

  public static List<List<String>> generateSheetTwoDataTables(int rows) {
    List<List<String>> table = new ArrayList<>();
    List<String> headers = Arrays.asList("CAR_ID", "CAR_DEALER", "PRICE");
    table.add(headers);
    for (int i = 0; i < rows; i++) {
      String id = String.valueOf(i);

      String carDealer =
          GenUtil.oneOf(
              "Huat Huat Car",
              "Best Auto",
              "Fast Car Ptd Ltd",
              "Dealer X",
              "Premium Auto",
              "Luxury Automobile",
              "Prestige Auto");
      String price = String.valueOf(GenUtil.randamIntRange(10, 1000) * 1000);
      table.add(Arrays.asList(id, carDealer, price));
    }
    return table;
  }

  public static void populateSheet(Workbook workbook, String sheetname, List<List<String>> table) {
    if (table.size() == 0) throw new IllegalArgumentException("table size must be > 0");
    Sheet sheet = workbook.createSheet(sheetname);
    // set column width for all columns
    for (int i = 0; i < table.get(0).size(); i++) {
      sheet.setColumnWidth(i, 6000);
    }

    CellStyle headerStyle = fontStyle(workbook, "Arial", 14, true);
    CellStyle rowStyle = fontStyle(workbook, "Arial", 14, false);

    Row header = sheet.createRow(0);
    populateRow(header, table.get(0), headerStyle);
    for (int i = 1; i < table.size(); i++) {
      Row row = sheet.createRow(i);
      populateRow(row, table.get(i), rowStyle);
    }
  }

  public static CellStyle fontStyle(
      Workbook workbook, String fontname, int heightPoints, boolean isBold) {
    CellStyle headerStyle = workbook.createCellStyle();

    Font font;
    if (workbook.getClass().getName().equals("org.apache.poi.hssf.usermodel.HSSFWorkbook")) {
      font = ((HSSFWorkbook) workbook).createFont();
    } else {
      font = ((XSSFWorkbook) workbook).createFont();
    }
    font.setFontName(fontname);
    font.setFontHeightInPoints((short) heightPoints);
    font.setBold(isBold);
    headerStyle.setFont(font);
    return headerStyle;
  }

  public static void populateRow(Row row, List<String> tableRow, CellStyle style) {
    for (int i = 0; i < tableRow.size(); i++) {
      Cell cell = row.createCell(i);
      cell.setCellValue(tableRow.get(i));
      cell.setCellStyle(style);
    }
  }

  public static void main(String[] args) throws IOException {
    Paths.get("target/samples").toFile().mkdirs();
    generateCarsExcelFiles(50, Paths.get("target/samples"));
  }
}
