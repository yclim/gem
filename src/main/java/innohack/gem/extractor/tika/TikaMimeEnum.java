package innohack.gem.extractor.tika;

public enum TikaMimeEnum {

    PDF("pdf"),
    MSEXCEL("vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    MSWORD("vnd.openxmlformats-officedocument.wordprocessingml.document"),
    CSV("csv"),
    UNKNOWN("unknown");

    private String mimeType;

    TikaMimeEnum(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
