package innohack.gem.entity;

import com.google.common.collect.Lists;
import innohack.gem.entity.gem.data.AbstractFeature;
import innohack.gem.entity.gem.data.CsvFeature;
import innohack.gem.entity.gem.data.ExcelFeature;
import innohack.gem.entity.gem.data.TikaFeature;
import innohack.gem.example.tika.TikaExcelParser;
import innohack.gem.example.tika.TikaMimeEnum;
import innohack.gem.example.tika.TikaPdfParser;
import innohack.gem.example.tika.TikaUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

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

  public Path getPath() { return path; }

  public void setPath(Path path) { this.path = path; }

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
    // TODO extract file's data
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    TikaUtil tikaUtil = new TikaUtil();

    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, path.toString());
    MediaType mediaType = null;
    try {
      mediaType = tikaUtil.getTika().getDetector().detect(TikaInputStream.get(path), metadata);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (mediaType.getSubtype().equals(TikaMimeEnum.PDF.getMimeType())) {


    } else if (mediaType.getSubtype().equals(TikaMimeEnum.MSWORD.getMimeType())) {


    } else if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLSX.getMimeType()) ||
        mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {

      extractExcel(mediaType);


    } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
      extractCSV();

    } else {
      System.out.println("the mediatype is " + mediaType );
    }

    return this;
  }

  // Perform extraction on csv
  public GEMFile extractCSV() {
    // TODO extract file's data
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    CsvFeature extractedData1 = new CsvFeature();
    extractedData1.extract(f);
    addData(extractedData1);

    System.out.println("*************Metadata****************");
    System.out.println(extractedData1.getMetadata().toString());

    System.out.println("*************HEADER****************");
    System.out.println(extractedData1.getHeader());

    System.out.println("*************RECORD****************");
    System.out.println(extractedData1.getContents());

    System.out.println("*************COLRECORD****************");
    System.out.println(extractedData1.getColRecords().toString());

    return this;
  }

  // Perform extraction on Excel
  public GEMFile extractExcel(MediaType mediaType) {
    // TODO extract file's data

    System.out.println("extractionExcel here");
    File f = new File(getAbsolutePath());
    extension = FilenameUtils.getExtension(f.getName());

    ExcelFeature extractedData1 = new ExcelFeature(mediaType);
    extractedData1.extract(f);
    addData(extractedData1);



    System.out.println("*************Metadata****************");
    System.out.println(extractedData1.getMetadata().toString());

    System.out.println("*************Sheets****************");
    System.out.println(extractedData1.getSheetFeatures().toString());


    return this;
  }

}
