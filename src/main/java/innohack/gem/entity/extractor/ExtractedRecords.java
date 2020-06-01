package innohack.gem.entity.extractor;

import com.beust.jcommander.internal.Lists;
import java.util.List;

public class ExtractedRecords {

  private List<String> headers;

  private List<List<String>> records;

  public ExtractedRecords() {
    headers = Lists.newArrayList();
    records = Lists.newArrayList();
  }

  public List<String> getHeaders() {
    return headers;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
  }

  public List<List<String>> getRecords() {
    return records;
  }

  public void setRecords(List<List<String>> records) {
    this.records = records;
  }

  public int size() {
    return records.size();
  }
}
