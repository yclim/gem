package innohack.gem.entity.gem.data;

import innohack.gem.entity.gem.util.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaMimeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/** Object to hold wrap extracted excel data */
public class ExcelFeature extends AbstractFeature {

  private Map<String, List<List<String>>> sheetFeatures;
  private MediaType mediaType;

  public ExcelFeature(MediaType mediaType) {
    super(Target.EXCEL);
    this.mediaType = mediaType;
  }

  public Map<String, List<List<String>>> getSheetFeatures() {
    return sheetFeatures;
  }

  @Override
  public void extract(File f) throws Exception {

    File file = new File(String.valueOf(f.toPath().toAbsolutePath()));
    Workbook workbook = WorkbookFactory.create(file);
    Metadata metadata = parseExcel(f.toPath(), mediaType);
    String[] metadataNames = metadata.names();

    for (String name : metadataNames) {
      System.out.println(name + " : " + metadata.get(name));
      addMetadata(name, metadata.get(name));
    }
    if (workbook.getNumberOfSheets() > 0) {
      sheetFeatures = new HashMap<>();
      for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
        String sheetName = sheetParser(workbook, i, sheetFeatures);
        System.out.println(
            "SheetNo: " + (i + 1) + "sheetFeatures is " + sheetFeatures.get(sheetName).toString());
      }
    }
    workbook.close();
  }

  private String sheetParser(
      Workbook workbook, int sheetNo, Map<String, List<List<String>>> sheetFeatures) {

    String sheetName = "";
    if (workbook != null) {
      Sheet workSheet = workbook.getSheetAt(sheetNo);

      sheetName = workSheet.getSheetName();

      // iterate through list of records
      Iterator<Row> rowIterator = workSheet.rowIterator();
      int totalRow = 0;
      List<List<String>> contents = new ArrayList<>();
      HashMap<String, ArrayList<String>> colRecords = new HashMap<String, ArrayList<String>>();
      HashMap<Integer, String> colMapper = new HashMap<Integer, String>();

      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();

        int colCount = 0;

        ArrayList<String> recordBuilder = new ArrayList<String>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
          Cell cell = row.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
          String value = FeatureExtractorUtil.cellValue(cell);
          recordBuilder.add(value);

          if (totalRow == 0) {
            FeatureExtractorUtil.buildHeaderMapperAndContent(
                value, colRecords, colMapper, colCount);

          } else {
            FeatureExtractorUtil.buildContent(value, colRecords, colMapper, colCount);
          }
          colCount++;
        }
        contents.add(recordBuilder);
        totalRow++;
      }
      sheetFeatures.put(sheetName, contents);
      System.out.println("Col is :" + colRecords.toString());

    } else {
      System.out.println("Not able to parse");
    }
    return sheetName;
    // close readers
  }

  private Metadata parseExcel(Path path, MediaType mediaType)
      throws IOException, TikaException, SAXException {

    // detecting the file type
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    FileInputStream inputstream =
        new FileInputStream(new File(String.valueOf(path.toAbsolutePath())));
    ParseContext pcontext = new ParseContext();

    // OOXml parser
    Parser parser = null;
    if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())) {
      parser = new OOXMLParser();
    } else if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
      parser = new OfficeParser();
    }

    if (parser != null) {
      parser.parse(inputstream, handler, metadata, pcontext);

    } else {
      System.out.println("No sutiable parser for this type");
    }
    inputstream.close();
    return metadata;
  }
}
