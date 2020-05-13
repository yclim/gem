package innohack.gem.entity.extractor;

import com.beust.jcommander.internal.Lists;
import innohack.gem.service.extract.Extractor;
import java.util.List;

public class ExtractConfig {
  
  private String name;
  private List<String> namesColumn;
  private List<TimestampColumn> timestampColumn;
  private Extractor extractor;

  public ExtractConfig() {
    namesColumn = Lists.newArrayList();
    timestampColumn = Lists.newArrayList();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getNamesColumn() {
    return namesColumn;
  }

  public List<TimestampColumn> getTimestampColumn() {
    return timestampColumn;
  }

  public void addColumnNames(String value) {
    namesColumn.add(value);
  }

  public void removeColumnName(String value) {
    namesColumn.remove(value);
  }

  public void addColumnTimestamp(TimestampColumn value) {
    timestampColumn.add(value);
  }

  public void removeColumnTimestamp(TimestampColumn value) {
    timestampColumn.remove(value);
  }

  public Extractor getExtractor() {
    return extractor;
  }

  public void setExtractor(Extractor extractor) {
    this.extractor = extractor;
  }

}
