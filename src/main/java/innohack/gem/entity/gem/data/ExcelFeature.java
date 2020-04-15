package innohack.gem.entity.gem.data;

import innohack.gem.example.tika.TikaExcelParser;
import innohack.gem.extractor.poi.PoiExcelParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted excel data */
public class ExcelFeature extends AbstractFeature {

  private TikaExcelParser excelParser;
  private PoiExcelParser poiParser;
  private Workbook workbook;
  private List<ExcelSheetFeature> sheetFeatures;

  public ExcelFeature() {
    super(Target.EXCEL);
  }


  public Workbook getWorkbook() {
    return workbook;
  }

  public void setWorkbook(Workbook workbook) {
    this.workbook = workbook;
  }

  public List<ExcelSheetFeature> getSheetFeatures() {
    return sheetFeatures;
  }

  public void setSheetFeatures(
      List<ExcelSheetFeature> sheetFeatures) {
    this.sheetFeatures = sheetFeatures;
  }

  @Override
  public void extract(File f) {
    // TODO extraction method for EXCEL
    // TODO extraction method for EXCEL

    excelParser = new TikaExcelParser(f.toPath());
    poiParser = new PoiExcelParser(f.toPath());
    workbook = poiParser.getWorkBook();
    // to get metadata first
    Metadata metadata = null;
    try {
      metadata = excelParser.parseExcel();

      String[] metadataNames = metadata.names();

      for (String name : metadataNames) {

        System.out.println(name + " : " + metadata.get(name));
        addMetadata(name, metadata.get(name));
      }

      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
          Sheet workSheet = workbook.getSheetAt(i);
          ExcelSheetFeature sheetFeature = new ExcelSheetFeature();
          sheetFeature.extract(f, i);
          sheetFeatures.add(sheetFeature);

      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (TikaException e) {
      e.printStackTrace();
    }
  }
}
