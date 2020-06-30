package innohack.gem.core.entity.extract;

import com.beust.jcommander.internal.Lists;
import java.util.ArrayList;
import java.util.List;

public class ExtractedRecords {

  private List<String> headers;

  private List<List<String>> records;

  private String group;

  public ExtractedRecords() {
    headers = Lists.newArrayList();
    records = Lists.newArrayList();
  }

  public ExtractedRecords(String group, List<String> headers, List<List<String>> records) {
    this.group = group;
    this.headers = headers;
    this.records = records;
  }

  public static ExtractedRecords empty(String group) {
    return new ExtractedRecords(group, new ArrayList<>(), new ArrayList<>());
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

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }
}
