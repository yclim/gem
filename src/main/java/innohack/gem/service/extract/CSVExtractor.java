package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import java.text.ParseException;
import java.util.List;

public class CSVExtractor extends AbstractExtractor {

  public CSVExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public ExtractedRecords extract(GEMFile f) throws Exception {
    f.extract();
    for (AbstractFeature feature : f.getData()) {
      if (feature instanceof CsvFeature) {
        List<String> extractColumns = getExtractConfig().getNamesColumn();
        List<TimestampColumn> extractTSColumns = getExtractConfig().getTimestampColumn();

        ExtractedRecords results = new ExtractedRecords();
        List<List<String>> rows = ((CsvFeature) feature).getTableData();
        for (int i = 1; i < rows.size(); i++) results.getRecords().add(Lists.newArrayList());
        List<String> columns = ((CsvFeature) feature).getHeaders();
        for (int i = 0; i < columns.size(); i++) {
          String column = columns.get(i);
          if (extractColumns.contains(column)) {
            populate(results, rows, i, column);
          }
          for (TimestampColumn extractTSColumn : extractTSColumns) {
            if (extractTSColumn.getFromColumn().equals(column)) {
              populate(
                  results,
                  rows,
                  i,
                  extractTSColumn.getName(),
                  extractTSColumn);
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
