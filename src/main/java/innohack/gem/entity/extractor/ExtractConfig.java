package innohack.gem.entity.extractor;

import com.beust.jcommander.internal.Lists;
import innohack.gem.service.extract.Extractor;
import java.util.List;

public class ExtractConfig {
  private String tableName;
  private List<String> columnNames;
  private List<TimestampColumn> timestampColumns;
  private int groupId;
  private Extractor extractor;
  private List<String> sheetNames;

  public ExtractConfig() {
    columnNames = Lists.newArrayList();
    timestampColumns = Lists.newArrayList();
    sheetNames = Lists.newArrayList();
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<String> getColumnNames() {
    return columnNames;
  }

  public List<TimestampColumn> getTimestampColumns() {
    return timestampColumns;
  }

  public void addColumnNames(String value) {
    columnNames.add(value);
  }

  public void removeColumnName(String value) {
    columnNames.remove(value);
  }

  public void addColumnTimestamp(TimestampColumn value) {
    timestampColumns.add(value);
  }

  public void removeColumnTimestamp(TimestampColumn value) {
    timestampColumns.remove(value);
  }

  public Extractor getExtractor() {
    return extractor;
  }

  public void setExtractor(Extractor extractor) {
    this.extractor = extractor;
  }

  public void addSheetNames(String value) {
    sheetNames.add(value);
  }

  public List<String> getsheetNames() {
    return this.sheetNames;
  }

  public void removeSheetNames(String value) {
    sheetNames.remove(value);
  }
}
