package innohack.gem.entity;

import com.google.common.collect.Lists;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import innohack.gem.entity.feature.ExcelFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.entity.feature.common.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaMimeEnum;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;

/** Container that keeps all data extracted from a file */
public class GEMFile implements Comparable<GEMFile> {
  private String fileName;
  private Long size;
  private String extension;
  private String directory;
  private List<AbstractFeature> data;
  private MediaType _mediaType;
  private File _file;

  public GEMFile(String fileName, String directory) {
    this.directory = directory;
    this.fileName = fileName;

    this._file = new File(getAbsolutePath());
    this.extension = FilenameUtils.getExtension(_file.getName());
    this.size = _file.length();
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

  public String getAbsolutePath() {
    return Paths.get(this.getDirectory(), this.getFileName()).toString();
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

  public List<AbstractFeature> getData() {
    return data;
  }

  public void setData(List<AbstractFeature> data) {
    this.data = data;
  }

  public String getMimeType() {
    if (_mediaType == null) {
      return "";
    } else {
      return _mediaType.toString();
    }
  }
  // Perform extraction
  public GEMFile extract() throws Exception {
    this._mediaType = FeatureExtractorUtil.extractMime(new TikaConfig(), this._file.toPath());

    String subtype = _mediaType.getSubtype();
    if (subtype.equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())
        || subtype.equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
      extractExcel(_mediaType);
    } else if (_mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
      extractCSV();
    }
    //  we always want to use Tika no matter what file type
    extractTika();

    return this;
  }

  // Perform extraction on csv
  public GEMFile extractCSV() throws Exception {
    CsvFeature extractedData1 = new CsvFeature();
    extractedData1.extract(this._file);
    addData(extractedData1);
    return this;
  }

  // Perform extraction on Excel
  public GEMFile extractExcel(MediaType mediaType) throws Exception {
    ExcelFeature extractedData1 = new ExcelFeature();
    extractedData1.extract(this._file);
    addData(extractedData1);
    return this;
  }

  public GEMFile extractTika() throws Exception {

    TikaFeature tikaFeature = new TikaFeature();
    tikaFeature.extract(this._file);
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
        && _file.length() == gemFile._file.length()
        && _file.lastModified() == gemFile._file.lastModified();
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, directory, _file.lastModified(), _file.length());
  }
}
