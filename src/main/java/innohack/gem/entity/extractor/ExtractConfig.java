package innohack.gem.entity.extractor;

import java.util.ArrayList;
import java.util.List;

public class ExtractConfig {
  private String name;
  private List<String> namesColumn;
  private List<TimestampColumn> timestampColumn;

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
    if (namesColumn == null) {
      namesColumn = new ArrayList<>();
    }
    namesColumn.add(value);
  }

  public void removeColumnName(String value) {
    if (namesColumn != null) {
      namesColumn.remove(value);
    }
  }

  public void addColumnTimestamp(TimestampColumn value) {
    if (timestampColumn == null) {
      timestampColumn = new ArrayList<>();
    }
    timestampColumn.add(value);
  }

  public void removeColumnTimestamp(TimestampColumn value) {
    if (timestampColumn != null) {
      timestampColumn.remove(value);
    }
  }
}
