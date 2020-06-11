package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.extractor.TimestampColumn;
import innohack.gem.core.entity.feature.AbstractFeature;
import innohack.gem.core.entity.feature.CsvFeature;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVExtractor extends AbstractExtractor {
  private String LABEL = "CSV Extractor";

  public CSVExtractor() {
    this(null);
  }

  public CSVExtractor(String headers) {
    setLabel(LABEL);
    setParams(
        Lists.newArrayList(new Parameter("Column Names", "", ParamType.STRING_LIST, headers)));
  }

  @Override
  public ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception {

    for (AbstractFeature feature : f.getData()) {
      if (feature instanceof CsvFeature) {
        String[] paramValues = getParams().get(0).getValue().split(",");
        List<String> extractColumns = Arrays.stream(paramValues).collect(Collectors.toList());

        ExtractedRecords results = new ExtractedRecords();
        List<List<String>> rows = ((CsvFeature) feature).getTableData();
        for (int i = 1; i < rows.size(); i++) results.getRecords().add(Lists.newArrayList());
        List<String> featureColumns = ((CsvFeature) feature).getHeaders();
        for (int i = 0; i < featureColumns.size(); i++) {
          String featureColumn = featureColumns.get(i);

          int foundIndex = extractColumns.indexOf(featureColumn);
          if (foundIndex > -1) {
            String columnName = extractConfig.getColumnNames().get(foundIndex);
            populate(results, rows, i, columnName);

            for (TimestampColumn extractTSColumn : extractConfig.getTimestampColumns()) {
              if (extractTSColumn.getFromColumn().equals(columnName)) {
                populate(results, rows, i, extractTSColumn.getName(), extractTSColumn);
              }
            }
          }
        }
        return results;
      }
    }
    throw new IllegalStateException("No CSV Feature found");
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
