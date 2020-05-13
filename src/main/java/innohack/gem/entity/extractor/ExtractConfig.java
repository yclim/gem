package innohack.gem.entity.extractor;

import com.beust.jcommander.internal.Lists;
import java.util.List;

public class ExtractConfig {
  private String name;
  private List<String> namesColumn;
  private List<TimestampColumn> timestampColumn;
  private List<String> sheetNames;

  public ExtractConfig() {
    namesColumn = Lists.newArrayList();
    timestampColumn = Lists.newArrayList();
    sheetNames = Lists.newArrayList();
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

  public List<String> getSheetNames() {
    return sheetNames;
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

  public void addSheetNames(String value) {
    sheetNames.add(value);
  }

  public void removeSheetNames(String value) {
    sheetNames.remove(value);
  }
}
