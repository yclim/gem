package innohack.gem.example.tika;

import innohack.gem.entity.GEMFile;
import innohack.gem.example.util.FileUtil;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TikaUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaUtil.class);
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
      LOGGER.error("error ex: " + ex.toString());
    }
  }

  /**
   * Walkpath to walk though a folder contains different files
   *
   * @param path of the folder which contains the files
   */
  public void walkPathAndParse(String path) throws Exception {

    List<Path> results = FileUtil.walkPath(path);

    for (Path result : results) {
      LOGGER.debug("each result is " + result.toAbsolutePath());
      Metadata metadata = new Metadata();
      metadata.set(Metadata.RESOURCE_NAME_KEY, result.toString());
      MediaType mimetype = null;

      try {
        mimetype = tika.getDetector().detect(TikaInputStream.get(result), metadata);
      } catch (IOException e) {
        e.printStackTrace();
      }

      TikaMimeEnum mimeType = determineMimeTypeAndParser(mimetype, result);
      LOGGER.debug(
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
  public TikaMimeEnum determineMimeTypeAndParser(MediaType mediaType, Path path) throws Exception {
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

      csvFile.extractCSV();

      return TikaMimeEnum.CSV;

    } else return TikaMimeEnum.UNKNOWN;
  }
}
