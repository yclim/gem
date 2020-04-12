package innohack.gem.filegen;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Code to generate PDF documents. PDF contains a header text and a list of records. that looks like
 * this:
 *
 * <p>PRODUCT INFORMATION :: PRODUCT_ID, PRODUCT_NAME, PRICE, QUANTITY p81035397,
 * product_ulqcboznxu, 7839.24, 689 p44218053, product_jsppfrleqq, 7900.89, 54 p89301633,
 * product_gjjuohwkwp, 754.96, 904
 */
public class PdfFileGenerator {

  public static void generateProductsPdfFiles(int numOfFiles, Path dest) throws IOException {
    String HEADER_TEXT = "PRODUCT INFORMATION :: PRODUCT_ID, PRODUCT_NAME, PRICE, QUANTITY";
    String fileNamePrefix = "product_info_";
    List<String> textLines = null;

    for (int i = 0; i < numOfFiles; i++) {
      textLines = new ArrayList<>();
      textLines.add(HEADER_TEXT);
      int numOfRecords = GenUtil.randomInt(15);
      String filename = fileNamePrefix + "_" + i + ".pdf";
      List<List<String>> dataTable = generateDataTable(numOfRecords);
      for (List<String> row : dataTable) {
        textLines.add(String.join(", ", row));
      }
      generateTextualPdfFiles(Paths.get(dest.toString(), filename), textLines);
    }
  }

  public static void generateTextualPdfFiles(Path path, List<String> textLines) throws IOException {
    PDDocument document = new PDDocument();
    PDPage page = new PDPage();
    document.addPage(page);

    PDPageContentStream cont = new PDPageContentStream(document, page);

    cont.setFont(PDType1Font.TIMES_ROMAN, 12);
    cont.beginText();
    cont.setLeading(14.5f); // ensure every text line will be in next line
    cont.newLineAtOffset(30, 700); // margin-left, margin-bottom

    for (String line : textLines) {
      cont.showText(line);
      cont.newLine();
    }

    cont.endText();
    cont.close();

    document.save(path.toString());
    document.close();
  }

  public static List<List<String>> generateDataTable(int rows) {
    List<List<String>> list = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      String id = String.format("p%08d", GenUtil.randomInt(99999999));
      String product = "product_" + GenUtil.randomString(10);
      String price = GenUtil.randomInt(9999) + "." + String.format("%02d", GenUtil.randomInt(99));
      String quantity = String.valueOf(GenUtil.randomInt(999));
      list.add(Arrays.asList(id, product, price, quantity));
    }
    return list;
  }

  public static void main(String[] args) throws IOException {
    generateProductsPdfFiles(100, Paths.get("target/samples"));
  }
}
