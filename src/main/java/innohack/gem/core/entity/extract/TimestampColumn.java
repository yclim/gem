package innohack.gem.core.entity.extract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimestampColumn {

  private static final String FORMAT = "yyyy/MM/dd HH:mm:ssZ";

  public static final String DEFAULT_TIMEZONE = "GMT+8";

  private String name;

  private String fromColumn;

  private String format;

  private String timezone; // Optional for users

  public TimestampColumn() {
    this(null, null, null);
  }

  public TimestampColumn(String name, String fromColumn, String format) {
    this(name, fromColumn, format, DEFAULT_TIMEZONE);
  }

  public TimestampColumn(String name, String fromColumn, String format, String timezone) {
    super();
    this.name = name;
    this.fromColumn = fromColumn;
    this.format = format;
    this.timezone = timezone;
  }

  public String format(String value) throws ParseException {
    DateFormat dateFormatter = new SimpleDateFormat(format);
    dateFormatter.setTimeZone(TimeZone.getTimeZone(timezone));
    DateFormat strFormatter = new SimpleDateFormat(FORMAT);
    strFormatter.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
    return strFormatter.format(dateFormatter.parse(value));
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

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
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
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TimestampColumn other = (TimestampColumn) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
}
