package innohack.gem.entity;

import com.google.common.collect.Lists;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import innohack.gem.entity.feature.ExcelFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.entity.feature.common.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaMimeEnum;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;

/** Container that keeps all data extracted from a file */
public class GEMFile implements Comparable<GEMFile> {
  private String fileName;
  private String directory;
  private Long size;
  private String extension;
  private List<AbstractFeature> data;
  private String mimeType;

  public GEMFile() {}

  public GEMFile(String fileName, String directory) {
    this.directory = directory;
    this.fileName = fileName;
    File file = new File(directory, fileName);
    this.extension = FilenameUtils.getExtension(fileName);
    this.size = file.length();
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

  // Perform extraction
  public GEMFile extract() throws Exception {
    File file = new File(directory, fileName);
    MediaType mediaType = FeatureExtractorUtil.extractMime(new TikaConfig(), file.toPath());
    this.mimeType = mediaType.toString();
    String subtype = mediaType.getSubtype();
    if (subtype.equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())
        || subtype.equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
      extractExcel(mediaType);
    } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
      extractCSV();
    }
    //  we always want to use Tika no matter what file type
    extractTika();

    return this;
  }

  // Perform extraction on csv
  public GEMFile extractCSV() throws Exception {
    File file = new File(directory, fileName);
    CsvFeature extractedData1 = new CsvFeature();
    extractedData1.extract(file);
    addData(extractedData1);
    return this;
  }

  // Perform extraction on Excel
  public GEMFile extractExcel(MediaType mediaType) throws Exception {
    File file = new File(directory, fileName);
    ExcelFeature extractedData1 = new ExcelFeature();
    extractedData1.extract(file);
    addData(extractedData1);
    return this;
  }

  public GEMFile extractTika() throws Exception {
    File file = new File(directory, fileName);
    TikaFeature tikaFeature = new TikaFeature();
    tikaFeature.extract(file);
    addData(tikaFeature);

    return this;
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
