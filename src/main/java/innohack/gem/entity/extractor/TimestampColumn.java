package innohack.gem.entity.extractor;

public class TimestampColumn {

  private String name;
  
  private String fromColumn;
  
  private String format;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFromColumn() {
    return fromColumn;
  }

  public void setFromColumn(String fromColumn) {
    this.fromColumn = fromColumn;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
  
}
