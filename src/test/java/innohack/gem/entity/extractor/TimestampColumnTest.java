package innohack.gem.entity.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import org.junit.jupiter.api.Test;

public class TimestampColumnTest {

  @Test
  public void testFormat() throws ParseException {
    TimestampColumn column = new TimestampColumn("to", "from", "yyyyMMdd HHmmss", "GMT+8");
    assertEquals("2020/05/12 18:46:14+0800", column.format("20200512 184614"));
    
    TimestampColumn column2 = new TimestampColumn("to", "from", "yyyyMMdd HHmmss", "UTC");
    assertEquals("2020/05/13 02:46:14+0800", column2.format("20200512 184614"));
    
    TimestampColumn column3 = new TimestampColumn("to", "from", "yyyy-MMM-dd HHmmss", "UTC");
    assertEquals("2020/05/13 02:46:14+0800", column3.format("2020-May-12 184614"));
  }
}
