package innohack.gem.filegen;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code to generate PDF documents.
 * PDF contains a header text and a list of records. that looks like this:
 *
 * PRODUCT INFORMATION :: PRODUCT_ID, PRODUCT_NAME, QUANTITY
 * p381356, product_381356, 3813560
 * p381357, product_381357, 3813570
 * p381358, product_381358, 3813580
 */
public class PdfFileGenerator {

    public static void generateTextualPdfFiles(Path path, List<String> textLines) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream cont = new PDPageContentStream(document, page);

        cont.setFont(PDType1Font.TIMES_ROMAN, 12);
        cont.beginText();
        cont.setLeading(14.5f); //ensure every text line will be in next line
        cont.newLineAtOffset(30, 700); //margin-left, margin-bottom

        for (String line: textLines) {
            cont.showText(line);
            cont.newLine();
        }

        cont.endText();
        cont.close();

        document.save(path.toString());
        document.close();
    }

    public static List<List<String>> generateDataTable(int start, int end) {
        List<List<String>> list = new ArrayList<>();
        for (int i = start; i < end; i++) {
            list.add(Arrays.asList("p" + i, "product_" + GenUtil.randomString(10), String.valueOf(i * 10)));
        }
        return list;
    }

    public static void generateProductsPdfFiles(int numOfFiles, Path dest) throws IOException {
        String HEADER_TEXT = "PRODUCT INFORMATION :: PRODUCT_ID, PRODUCT_NAME, QUANTITY";
        String fileNamePrefix = "product_info_";
        List<String> textLines = null;

        for (int i = 0; i < numOfFiles; i++) {
            textLines = new ArrayList<>();
            textLines.add(HEADER_TEXT);
            int startOffset = GenUtil.randomInt(1000000);
            int numOfRecords = GenUtil.randomInt(15);
            String filename = fileNamePrefix + "_" + i + ".pdf";
            List<List<String>> dataTable = generateDataTable(startOffset, startOffset + numOfRecords);
            for (List<String> row: dataTable) {
                textLines.add(String.join(", ", row));
            }
            if (!dest.toFile().exists()) {
                Files.createDirectory(dest);
            }
            generateTextualPdfFiles(Paths.get(dest.toString(), filename), textLines);
        }
    }

    public static void main(String[] args) throws IOException {
        generateProductsPdfFiles(100, Paths.get("target/samples"));
    }
}
