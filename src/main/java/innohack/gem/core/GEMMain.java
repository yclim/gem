package innohack.gem.core;

import com.beust.jcommander.internal.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.Project;
import innohack.gem.core.entity.extractor.ExtractConfig;
import innohack.gem.core.entity.extractor.ExtractedRecords;
import innohack.gem.core.entity.feature.CsvFeature;
import innohack.gem.core.entity.feature.ExcelFeature;
import innohack.gem.core.entity.feature.TikaFeature;
import innohack.gem.core.entity.feature.common.FeatureExtractorUtil;
import innohack.gem.core.entity.rule.Group;
import innohack.gem.core.entity.rule.rules.Rule;
import innohack.gem.example.tika.TikaMimeEnum;
import innohack.gem.service.ExtractService;
import innohack.gem.service.extract.AbstractExtractor;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GEMMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(GEMMain.class);

  public static List<ExtractedRecords> process(File file, Project project) throws Exception {
    GEMFile gemFile = extractFeature(file);
    List<Group> matchedGroup = classifyGroup(gemFile, project);
    return extract(gemFile, matchedGroup);
  }

  public static GEMFile extractFeature(File file) throws Exception {
    GEMFile gemFile = new GEMFile(file.getAbsolutePath());
    try {
      MediaType mediaType = new FeatureExtractorUtil().extractMime(new TikaConfig(), file.toPath());
      gemFile.setMimeType(mediaType.toString());
      String subtype = mediaType.getSubtype();
      if (subtype.equals(TikaMimeEnum.MSEXCELXLSX.getMimeType())
          || subtype.equals(TikaMimeEnum.MSEXCELXLS.getMimeType())) {
        ExcelFeature excelFeature = new ExcelFeature();
        excelFeature.extract(file);
        gemFile.getData().add(excelFeature);
      } else if (mediaType.getSubtype().equals(TikaMimeEnum.CSV.getMimeType())) {
        CsvFeature csvFeature = new CsvFeature();
        csvFeature.extract(file);
        gemFile.getData().add(csvFeature);
      }
      //  we always want to use Tika no matter what file type
      TikaFeature tikaFeature = new TikaFeature();
      tikaFeature.extract(file);
      gemFile.getData().add(tikaFeature);
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      gemFile.setErrorMessage(sw.toString());
      LOGGER.error("Error in extract", e);
      throw e;
    }
    return gemFile;
  }

  private static List<Group> classifyGroup(GEMFile file, Project project) {
    List<Group> matchedGroup = new ArrayList<Group>();
    project.getGroups().stream()
        .forEach(
            group -> {
              boolean match = true;
              List<Rule> rules = group.getRules();
              for (Rule rule : rules) {
                if (!rule.check(file)) {
                  match = false;
                  break;
                }
              }
              if (match) {
                matchedGroup.add(group);
              }
            });
    return matchedGroup;
  }

  private static List<ExtractedRecords> extract(GEMFile file, List<Group> matchedGroup) {

    List<ExtractedRecords> results = Lists.newArrayList();
    matchedGroup.stream()
        .forEach(
            group -> {
              ExtractConfig config = group.getExtractConfig();
              AbstractExtractor extractor = config.getExtractor();
              ExtractedRecords records = null;
              try {
                records = extractor.extract(file, config);
              } catch (Exception e) {
                e.printStackTrace();
              }
              results.add(records);
            });
    return results;
  }
}
