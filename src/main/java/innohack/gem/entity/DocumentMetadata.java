package innohack.gem.entity;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class DocumentMetadata {
    private String name;
    private String contentType;
    private Long size;
    private String extension;
    private String filePath;

    public DocumentMetadata() {
    }

    public DocumentMetadata(MultipartFile file) {
        this.name = file.getOriginalFilename();
        this.size = file.getSize();
        this.extension = FilenameUtils.getExtension(file.getOriginalFilename());
        this.contentType = file.getContentType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "DocumentMetadata{\n" +
                "name='" + name + '\'' + "\n" +
                ", contentType='" + contentType + '\'' + "\n" +
                ", size=" + size + "\n" +
                ", extension='" + extension + '\'' + "\n" +
                ", filePath='" + filePath + '\'' + "\n" +
                '}';
    }
}