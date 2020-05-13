package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.ExcelFeature;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class ExcelExtractor extends AbstractExtractor {

  public ExcelExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public ExtractedRecords extract(GEMFile f) throws Exception {
    f.extract();

    for (AbstractFeature feature : f.getData()) {

      if (feature instanceof ExcelFeature) {
        ExtractedRecords results = new ExtractedRecords();

        List<String> excelSheetNames = getExtractConfig().getsheetNames();
        List<String> extractColumns = getExtractConfig().getColumnNames();
        List<TimestampColumn> extractTSColumns = getExtractConfig().getTimestampColumns();

        Map<String, List<List<String>>> sheetTables = ((ExcelFeature) feature).getSheetTableData();

        for (String excelSheetName : excelSheetNames) {
          if (sheetTables.containsKey(excelSheetName)) {
            List<List<String>> rows = sheetTables.get(excelSheetName);

            for (int i = 1; i < rows.size(); i++) results.getRecords().add(Lists.newArrayList());
            // for getting the header for each sheet
            List<String> columns = rows.get(0);

            for (int j = 0; j < columns.size(); j++) {
              String column = columns.get(j);
              if (extractColumns.contains(column)) {
                populate(results, rows, j, column);
              }
              for (TimestampColumn extractTSColumn : extractTSColumns) {
                if (extractTSColumn.getFromColumn().equals(column)) {
                  populate(results, rows, j, extractTSColumn.getName(), extractTSColumn);
                }
              }
            }
          }
        }
        return results;
      }
    }
    throw new IllegalStateException("No Excel Feature found");
  }

  private void populate(
      ExtractedRecords results, List<List<String>> rows, int columnIdx, String name)
      throws ParseException {
    populate(results, rows, columnIdx, name, null);
  }

  private void populate(
      ExtractedRecords results,
      List<List<String>> rows,
      int columnIdx,
      String name,
      TimestampColumn tsColumn)
      throws ParseException {

    results.getHeaders().add(name);
    for (int i = 1; i < rows.size(); i++) {
      String value = rows.get(i).get(columnIdx);
      if (tsColumn != null) {
        value = tsColumn.format(value);
      }
      results.getRecords().get(i - 1).add(value);
    }
  }
}
