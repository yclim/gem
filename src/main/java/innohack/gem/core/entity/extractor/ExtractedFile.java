package innohack.gem.core.entity.extractor;

public class ExtractedFile {

  private String filename;

  private String absolutePath;

  private int count;

  public ExtractedFile() {}

  public ExtractedFile(String filename, String absolutePath, int count) {
    super();
    this.filename = filename;
    this.absolutePath = absolutePath;
    this.count = count;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
