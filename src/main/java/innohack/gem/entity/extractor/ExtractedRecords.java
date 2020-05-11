package innohack.gem.entity.extractor;

import java.util.List;

public class ExtractedRecords {

  private List<String> headers;

  private List<List<String>> records;

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

}
