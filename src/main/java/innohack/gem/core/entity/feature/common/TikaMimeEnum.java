package innohack.gem.core.entity.feature.common;

public enum TikaMimeEnum {
  PDF("pdf"),
  MSEXCELXLSX("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  MSEXCELXLS("vnd.ms-excel"),
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
