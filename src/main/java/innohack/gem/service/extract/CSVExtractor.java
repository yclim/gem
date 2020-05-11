package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CSVExtractor extends AbstractExtractor {

  public CSVExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public ExtractedRecords extract(GEMFile f) throws Exception {
    for(AbstractFeature feature: f.getData()) {
      if(feature instanceof CsvFeature) {
        List<String> extractColumns = getExtractConfig().getNamesColumn();
        List<TimestampColumn> extractTSColumns = getExtractConfig().getTimestampColumn();
        
        ExtractedRecords results = new ExtractedRecords();
        List<List<String>> rows = ((CsvFeature) feature).getTableData();
        for(int i=0; i<rows.size(); i++)
          results.getRecords().add(Lists.newArrayList());
        List<String> columns = ((CsvFeature) feature).getHeaders();
        for(int i=0; i<columns.size(); i++) {
          String column = columns.get(i);
          if(extractColumns.contains(column)) {
            populate(results, rows, i, column);
          }
          for(TimestampColumn extractTSColumn: extractTSColumns) {
            if(extractTSColumn.getFromColumn().equals(column)) {
              populate(results, rows, i, extractTSColumn.getName(), extractTSColumn.getFormat());
            }
          }
        }
        return results;
      }
    }
    throw new IllegalStateException("No CSV Feature found");
  }

  private void populate(ExtractedRecords results, List<List<String>> rows, int columnIdx, String name) throws ParseException {
    populate(results, rows, columnIdx, name, null);
  }
  
  private void populate(ExtractedRecords results, List<List<String>> rows, int columnIdx, String name, String dateFormat) throws ParseException {
    DateFormat formatter = null;
    if(dateFormat!=null)
      formatter = new SimpleDateFormat(dateFormat);
    
    results.getHeaders().add(name);
    for(int i=0; i<rows.size(); i++) {
      String value = rows.get(i).get(columnIdx);
      if(formatter!=null) {
        value = String.valueOf(formatter.parse(value).getTime());
      }
      results.getRecords().get(i).add(value);
    }
  }
  
}
