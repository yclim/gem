package innohack.gem.entity.extractor;

import java.util.ArrayList;
import java.util.List;

public class ExtractConfig {
  private String name;
  private List<String> namesColumn;
  private List<String> timestampColumn;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public void addColumnTimestamp(String value) {
    if (timestampColumn == null) {
      timestampColumn = new ArrayList<>();
    }
    timestampColumn.add(value);
  }

  public void removeColumnTimestamp(String value) {
    if (timestampColumn != null) {
      timestampColumn.remove(value);
    }
  }
}
