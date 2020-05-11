package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.TimestampColumn;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import java.util.List;

public class CSVExtractor extends AbstractExtractor {

  public CSVExtractor(ExtractConfig extractConfig) {
    this.setExtractConfig(extractConfig);
  }

  @Override
  public List<List<String>> extract(GEMFile f) throws Exception {
    for(AbstractFeature feature: f.getData()) {
      if(feature instanceof CsvFeature) {
        List<String> extractColumns = getExtractConfig().getNamesColumn();
        List<TimestampColumn> extractTSColumns = getExtractConfig().getTimestampColumn();
        
        List<List<String>> results = Lists.newArrayList();
        List<List<String>> rows = ((CsvFeature) feature).getTableData();
        List<String> columns = ((CsvFeature) feature).getHeaders();
        for(int i=0; i<columns.size(); i++) {
          String column = columns.get(i);
          if(extractColumns.contains(column)) {
            
          }
        }
        
        return null;
      }
    }
    return null;
  }
}
