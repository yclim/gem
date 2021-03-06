package innohack.gem.core.entity;

import com.google.common.collect.Lists;
import innohack.gem.core.feature.AbstractFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Container that keeps all data extracted from a file */
public class GEMFile implements Comparable<GEMFile> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GEMFile.class);

  private String fileName;
  private String directory;
  private Long size;
  private String extension;
  private List<AbstractFeature> data;
  private String mimeType;
  private String lastModifiedTime;
  private String creationTime;
  private String errorMessage;

  private transient boolean extracted;

  public GEMFile() {}

  public GEMFile(String fileName, String directory) {
    this(new File(directory, fileName).getAbsolutePath());
  }

  public GEMFile(String filePath) {
    data = new ArrayList<>();
    File file = new File(filePath);
    this.directory = file.getParentFile().getAbsolutePath();
    this.fileName = file.getName();
    this.extension = FilenameUtils.getExtension(fileName);
    this.size = file.length();
    try {
      BasicFileAttributes attributes =
          Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      this.creationTime =
          DateTimeFormatter.ISO_DATE_TIME.format(
              LocalDateTime.ofInstant(
                  attributes.creationTime().toInstant(), ZoneId.systemDefault()));
      this.lastModifiedTime =
          DateTimeFormatter.ISO_DATE_TIME.format(
              LocalDateTime.ofInstant(
                  attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Start of getter setter */
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
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

  public List<AbstractFeature> getData() {
    return data;
  }

  public void setData(List<AbstractFeature> data) {
    this.data = data;
  }

  public String getLastModifiedTime() {
    return lastModifiedTime;
  }

  public void setLastModifiedTime(String lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }
  /* End of getter setter */

  public String getAbsolutePath() {
    return new File(this.getDirectory(), this.getFileName()).getAbsolutePath();
  }

  public void addAllData(List<AbstractFeature> data) {
    if (this.data == null) {
      this.data = Lists.newArrayList();
    }
    this.data.addAll(data);
  }

  public void addData(AbstractFeature data) {
    if (this.data == null) {
      this.data = Lists.newArrayList();
    }
    this.data.add(data);
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public int compareTo(GEMFile gemFile) {
    if (this.equals(gemFile)) {
      return 0;
    } else {
      return this.getAbsolutePath().compareTo(gemFile.getAbsolutePath());
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GEMFile)) return false;
    GEMFile gemFile = (GEMFile) o;
    return fileName.equals(gemFile.fileName)
        && directory.equals(gemFile.directory)
        && size.equals(gemFile.size)
        && extension.equals(gemFile.extension);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, directory, size, extension, data);
  }
}
