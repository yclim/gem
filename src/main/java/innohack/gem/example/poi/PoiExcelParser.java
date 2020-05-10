package innohack.gem.example.poi;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class PoiExcelParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(PoiExcelParser.class);
  private Path filePath;

  public PoiExcelParser(Path filePath) {
    LOGGER.debug("POI filepath is " + filePath);
    this.filePath = filePath;
  }

  /**
   * this is to get a Workbook back using a filePath
   *
   * @return Workbook
   */
  public Workbook getWorkBook() {

    // Creating a Workbook from an Excel file (.xls or .xlsx)
    Workbook workbook = null;
    try {
      workbook = WorkbookFactory.create(new File(String.valueOf(this.filePath.toAbsolutePath())));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Retrieving the number of sheets in the Workbook
    LOGGER.debug("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

    return workbook;
  }
}
