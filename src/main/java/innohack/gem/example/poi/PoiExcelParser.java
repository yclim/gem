package innohack.gem.example.poi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class PoiExcelParser {

  private Path filePath;

  public PoiExcelParser(Path filePath) {
    System.out.println("POI filepath is " + filePath);
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
    System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

    return workbook;
  }
}
