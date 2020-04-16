package innohack.gem.entity;

import com.google.common.collect.Lists;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.CsvFeature;
import innohack.gem.entity.feature.ExcelFeature;
import innohack.gem.entity.feature.common.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaMimeEnum;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class GEMFile {
  private String fileName;
  private String contentType;
  private Long size;
  private String extension;

  private Path path;

  private String directory;
  private Collection<AbstractFeature> data;

  public GEMFile(String fileName, String directory) {
    this.directory = directory;
    this.fileName = fileName;
  }

  public GEMFile(Path path) {
    this.directory = path.getParent().toString();
    this.fileName = path.getFileName().toString();
    this.path = path;
  }

  public GEMFile(MultipartFile file) {
    this.fileName = file.getOriginalFilename();
    this.size = file.getSize();
    this.extension = FilenameUtils.getExtension(file.getOriginalFilename());
    this.contentType = file.getContentType();
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
    return getAbsolutePath(this.getFileName(), this.getDirectory());
  }

  public static String getAbsolutePath(String name, String directory) {
    return directory + "/" + name;
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

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public void addAllData(Collection<AbstractFeature> data) {
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

  public Collection<AbstractFeature> getData() {
    return data;
  }

  public void setData(Collection<AbstractFeature> data) {
    this.data = data;
  }

  // Perform extraction
  public GEMFile extract() {
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    try {
      TikaConfig config = new TikaConfig();
      MediaType mediaType = FeatureExtractorUtil.extractMime(config, f.toPath());
      String subtype = mediaType.getSubtype();
      System.out.println(subtype);
      if (subtype.equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())
          || subtype.equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
        extractExcel(mediaType);
      } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
        extractCSV();
      } else {
        System.out.println("unsupported: " + mediaType);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return this;
  }

  // Perform extraction on csv
  public GEMFile extractCSV() throws Exception {
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    CsvFeature extractedData1 = new CsvFeature();
    extractedData1.extract(f);
    addData(extractedData1);

    System.out.println("*************Metadata****************");
    System.out.println(extractedData1.getMetadata().toString());

    System.out.println("*************HEADER****************");
    System.out.println(extractedData1.getHeaders());

    System.out.println("*************RECORD****************");
    System.out.println(extractedData1.getTableData());

    return this;
  }

  // Perform extraction on Excel
  public GEMFile extractExcel(MediaType mediaType) throws Exception {

    System.out.println("extractionExcel here");
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    ExcelFeature extractedData1 = new ExcelFeature();
    extractedData1.extract(f);
    addData(extractedData1);

    System.out.println("*************Metadata****************");
    System.out.println(extractedData1.getMetadata().toString());

    System.out.println("*************Sheets****************");
    System.out.println(extractedData1.getSheetTableData().toString());

    return this;
  }
}
