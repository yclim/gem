package innohack.gem.example.tika;

public enum TikaMimeEnum {
  PDF("pdf"),
  MSEXCEL("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  MSWORD("vnd.openxmlformats-officedocument.wordprocessingml.document"),
  CSV("csv"),
  TEXT("text"),
  UNKNOWN("unknown");

  private String mimeType;

  TikaMimeEnum(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }
}
