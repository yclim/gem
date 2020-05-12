package innohack.gem.filegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateMockFiles {

  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateMockFiles.class);
  private static final Path defaultDest = Paths.get("target/samples/");
  private static final int defaultNumOfFiles = 20;

  public static void main(String[] args) throws IOException {
    Path dest = defaultDest;
    int numOfFiles = defaultNumOfFiles;
    if (args.length == 2) {
      dest = Paths.get(args[0]);
      numOfFiles = Integer.parseInt(args[1]);
    }

    LOGGER.info("dest: {}", dest);
    LOGGER.info("numOfFiles: {}", numOfFiles);

    if (!dest.toFile().exists()) {
      Files.createDirectory(dest);
    }

    PdfFileGenerator.generateProductsPdfFiles(numOfFiles, dest);
    CsvFileGenerator.generateCustomerCsvFiles(numOfFiles, dest);
    ExcelFileGenerator.generateCarsExcelFiles(numOfFiles, dest);
  }
}
