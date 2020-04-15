package innohack.gem.example.tika;

import com.opencsv.exceptions.CsvException;
import innohack.gem.example.Util.FileUtil;
import innohack.gem.entity.GEMFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.xml.sax.SAXException;

public class TikaUtil {

  private TikaConfig tika;

  public TikaConfig getTika() {
    return tika;
  }

  public void setTika(TikaConfig tika) {
    this.tika = tika;
  }

  public TikaUtil() {
    try {
      tika = new TikaConfig();

    } catch (IOException | TikaException ex) {
      System.out.println("error ex: " + ex.toString());
    }
  }

  /**
   * Walkpath to walk though a folder contains different files
   *
   * @param path of the folder which contains the files
   */
  public void walkPathAndParse(String path) {

      List<Path> results = FileUtil.walkPath(path);

      for (Path result : results) {
        System.out.println("each result is " + result.toAbsolutePath());
        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, result.toString());
        MediaType mimetype = null;

        try {
          mimetype = tika.getDetector().detect(TikaInputStream.get(result), metadata);
        } catch (IOException e) {
          e.printStackTrace();
        }

        TikaMimeEnum mimeType = determineMimeTypeAndParser(mimetype, result);
        System.out.println(
            "result is " + result.toAbsolutePath() + " mimeType is " + mimeType.getMimeType());
      }

  }



  /**
   * Walkpath to walk though a folder contains different files
   *
   * @param path of the folder which contains the files
   */
  public MediaType extractMime(Path path) {

    System.out.println("each result is " + path.toAbsolutePath());
    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, path.toString());
    MediaType mimeType = null;
    try {
      mimeType = tika.getDetector().detect(TikaInputStream.get(path), metadata);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mimeType;
  }

  /**
   * determineMimeTypeAndParser after finding out the MIME type, determine the type of tika/poi
   * parser to parse
   *
   * @param mediaType MIME type of the file
   * @param path path of the folder been point to
   * @return of the mimetype found
   */
  public TikaMimeEnum determineMimeTypeAndParser(MediaType mediaType, Path path) {
    // need to force find all the type here

    if (mediaType.getSubtype().equals(TikaMimeEnum.PDF.getMimeType())) {
      TikaPdfParser pdfParser = new TikaPdfParser(path);
      try {
        pdfParser.parsePDF();
      } catch (IOException | TikaException | SAXException e) {
        e.printStackTrace();
      }
      return TikaMimeEnum.PDF;

    } else if (mediaType.getSubtype().equals(TikaMimeEnum.MSWORD.getMimeType())) {
      return TikaMimeEnum.MSWORD;

    } else if (mediaType.getSubtype().equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())) {
      TikaExcelParser excelParser = new TikaExcelParser(path, mediaType);
      try {
        excelParser.parseExcel();
      } catch (IOException | TikaException | SAXException e) {
        e.printStackTrace();
      }
      return TikaMimeEnum.MSEXCELXLSX;

    } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
      GEMFile csvFile = new GEMFile(path.getFileName().toString(), path.getParent().toString());
      try {
        csvFile.extractCSV();
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (TikaException e) {
        e.printStackTrace();
      } catch (CsvException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      return TikaMimeEnum.CSV;

    } else return TikaMimeEnum.UNKNOWN;
  }

}
