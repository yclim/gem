package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.ExcelFeature;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelExtractor extends AbstractExtractor {

  public ExcelExtractor() {
    this(null, null);
  }

  public ExcelExtractor(String sheetName, String columnNames) {
    Parameter param1 = new Parameter("Sheet Name", "", ParamType.STRING, sheetName);
    Parameter param2 = new Parameter("Column Names", "", ParamType.STRING_LIST, columnNames);

    setParams(Lists.newArrayList(param1, param2));
  }

  @Override
  public ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception {
    f.extract();

    for (AbstractFeature feature : f.getData()) {

      if (feature instanceof ExcelFeature) {
        ExtractedRecords results = new ExtractedRecords();

        String excelSheetName = getParams().get(0).getValue();
        String[] paramValues = getParams().get(1).getValue().split(",");
        List<String> extractColumns = Arrays.stream(paramValues).collect(Collectors.toList());

        results.setSheetName(excelSheetName);

        Map<String, List<List<String>>> sheetTables = ((ExcelFeature) feature).getSheetTableData();

        if (sheetTables.containsKey(excelSheetName)) {
          List<List<String>> rows = sheetTables.get(excelSheetName);

          for (int i = 1; i < rows.size(); i++) results.getRecords().add(Lists.newArrayList());
          // for getting the header for each sheet
          List<String> columns = rows.get(0);

          for (int j = 0; j < columns.size(); j++) {
            String column = columns.get(j);
            int foundIndex = extractColumns.indexOf(column);
            if (foundIndex > -1) {
              String columnName = extractConfig.getColumnNames().get(foundIndex);
              populate(results, rows, j, columnName);

              for (TimestampColumn extractTSColumn : extractConfig.getTimestampColumns()) {
                if (extractTSColumn.getFromColumn().equals(columnName)) {
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
