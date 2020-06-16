package innohack.gem.core.entity.extract;

import com.beust.jcommander.internal.Lists;
import java.util.List;

public class ExtractedRecords {

  private List<String> headers;

  private List<List<String>> records;

  private List<String> groups;

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

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }
}
