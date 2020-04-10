package innohack.gem.filegen;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Code to generate CSV files with value quoted that look like this:
 *
 * "Customer ID","Customer Name","Gender","Address","Contact Number","Email"
 * "11567009","KEIRA","M","Chua Chu Kang Ave 69 #17-612","93679790","KEIRA@outlook.com"
 * "23568420","ELEANOR","F","Yio Chu Kang Ave 69 #09-978","94413844","ELEANOR@hotmail.com"
 * "56554231","HOLLY","F","Jurong East Street 19 #19-303","96535720","HOLLY@outlook.com"
 */

public class CsvFileGenerator {

    public static void generateCustomerCsvFiles(int numOfFiles, Path dest) throws FileNotFoundException {
        int numOfLines = GenUtil.randomInt(300);
        String filenamePrefix = "customer_";
        for (int i = 0; i < numOfFiles; i++) {
            String filename = filenamePrefix + i + ".csv";
            GenUtil.writeToFile(
                    generateDataTables(numOfLines).stream()
                            .map(CsvFileGenerator::toCsv)
                            .map(row -> String.join(",", row))
                            .collect(Collectors.toList()), dest, filename
            );

        }
    }

    public static List<List<String>> generateDataTables(int rows) {
        List<List<String>> table = new ArrayList<>();
        List<String> headers =
                Arrays.asList("Customer ID", "Customer Name", "Gender", "Address", "Contact Number", "Email");
        table.add(headers);
        for (int i = 0; i < rows; i++) {
            String custId = String.format("c%08d",GenUtil.randomInt(99999999));
            String name = GenUtil.randomName();
            String gender = GenUtil.oneOf("M", "F");
            String address = GenUtil.oneOf("Ang Mo Kio", "Yio Chu Kang", "Compassvale", "Chua Chu Kang", "Jurong East", "Bishan")
                    + " " + GenUtil.oneOf("Ave", "Street")
                    + " " + GenUtil.randomInt(100)
                    + " " + String.format("#%02d-%03d", GenUtil.randomInt(40), GenUtil.randomInt(999));
            String contactNumber = String.valueOf(GenUtil.randamIntRange(90000000, 99999999));
            String email = name + "@" + GenUtil.oneOf("gmail.com", "yahoo.com", "hotmail.com", "outlook.com");
            table.add(Arrays.asList(custId, name, gender, address, contactNumber, email));
        }
        return table;
    }

    public static List<String> toCsv(List<String> row) {
        // just add double quotes for each cell for now
        return row.stream()
            .map(col -> String.format("\"%s\"", col))
            .collect(Collectors.toList());
    }

    public static void main(String[] args) throws FileNotFoundException {
        generateCustomerCsvFiles(100, Paths.get("target/samples"));
    }
}
