package innohack.gem.core;

import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.feature.AbstractFeature;
import innohack.gem.core.feature.CsvFeature;
import innohack.gem.core.feature.ExcelFeature;
import innohack.gem.core.feature.TikaFeature;
import innohack.gem.core.feature.common.FeatureExtractorUtil;
import innohack.gem.example.tika.TikaMimeEnum;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GEMMain {

  static final String GROUP_NIL = "NIL";

  private static final Logger LOGGER = LoggerFactory.getLogger(GEMMain.class);

  /**
   * Extract feature from file, determine the group, and extract structured data from it Function
   * will produce ExtractedRecords for each group the file is classified to
   *
   * @param file
   * @param project
   * @return ExtractedRecords for each group the file is classified to
   */
  public static List<ExtractedRecords> process(File file, Project project) {
    GEMFile gemFile = extractFeature(file);
    List<Group> matchedGroup = classifyGroup(gemFile, project);
    return extract(gemFile, matchedGroup);
  }

  public static GEMFile extractFeature(File file) {
    GEMFile gemFile = new GEMFile(file.getAbsolutePath());

    // we always want to use Tika no matter what file type
    TikaFeature tikaFeature = new TikaFeature();
    addFeature(tikaFeature, file, gemFile);

    MediaType mediaType = null;
    try {
      mediaType = new FeatureExtractorUtil().extractMime(new TikaConfig(), file.toPath());
      gemFile.setMimeType(mediaType.toString());
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      gemFile.setErrorMessage(sw.toString());
      LOGGER.warn("Error in extracting MediaType: " + e.getMessage());
    }

    if (mediaType != null) {
      String subtype = mediaType.getSubtype();
      if (subtype.equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())
          || subtype.equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
        ExcelFeature excelFeature = new ExcelFeature();
        addFeature(excelFeature, file, gemFile);
      } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
        CsvFeature csvFeature = new CsvFeature();
        addFeature(csvFeature, file, gemFile);
      }
    }

    return gemFile;
  }

  private static void addFeature(AbstractFeature feature, File file, GEMFile gemFile) {
    try {
      feature.extract(file);
      gemFile.getData().add(feature);
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println(feature.getClass().getSimpleName());
      e.printStackTrace(pw);
      String prevError = gemFile.getErrorMessage() != null ? gemFile.getErrorMessage() : "";
      gemFile.setErrorMessage(prevError + sw.toString());
      LOGGER.warn("Error in extracting Feature: " + e.getMessage());
    }
  }

  private static List<Group> classifyGroup(GEMFile file, Project project) {
    return project.getGroups().stream()
        .filter(group -> group.getRules().stream().allMatch(r -> r.check(file)))
        .collect(Collectors.toList());
  }

  private static List<ExtractedRecords> extract(GEMFile file, List<Group> matchedGroup) {
    if (matchedGroup.size() == 0) {
      return Arrays.asList(ExtractedRecords.empty(GROUP_NIL));
    }

    return matchedGroup.stream()
        .map(
            group -> {
              if (group.getExtractConfig() != null
                  && group.getExtractConfig().getExtractor() != null) {
                ExtractConfig config = group.getExtractConfig();
                try {
                  ExtractedRecords records = config.getExtractor().extract(file, config);
                  records.setGroup(group.getName());
                  return records;
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              } else {
                return ExtractedRecords.empty(group.getName());
              }
            })
        .collect(Collectors.toList());
  }
}
