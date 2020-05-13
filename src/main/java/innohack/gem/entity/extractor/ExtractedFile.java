package innohack.gem.entity.extractor;

public class ExtractedFile {

  private String filename;

  private int count;

  public ExtractedFile() {}

  public ExtractedFile(String filename, int count) {
    super();
    this.filename = filename;
    this.count = count;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
