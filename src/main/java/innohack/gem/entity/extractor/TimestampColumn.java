package innohack.gem.entity.extractor;

public class TimestampColumn {

  private String name;
  
  private String fromColumn;
  
  private String format;
  
  
  public TimestampColumn() {
    
  }

  public TimestampColumn(String name, String fromColumn, String format) {
    super();
    this.name = name;
    this.fromColumn = fromColumn;
    this.format = format;
  }

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TimestampColumn other = (TimestampColumn) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
  
}
