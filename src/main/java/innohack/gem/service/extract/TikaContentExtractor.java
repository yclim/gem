package innohack.gem.service.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports both BLOB and Tabular extraction. Example of tabular extraction: Data:
 * Id,Sender,Review,Time Regex: (.*?),(.*?),(.*?),(.*)
 *
 * @author TC
 */
public class TikaContentExtractor extends AbstractExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaContentExtractor.class);

  public TikaContentExtractor() {
    this(null);
  }

  public TikaContentExtractor(String regex) {
    setParams(Lists.newArrayList(new Parameter("Content", "", ParamType.STRING, regex)));
  }

  @Override
  public ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception {
    f.extract();
    for (AbstractFeature feature : f.getData()) {
      if (feature instanceof TikaFeature) {
        String regexString = getParams().get(0).getValue();

        ExtractedRecords results = new ExtractedRecords();
        String tikaContent = ((TikaFeature) feature).getContent();
        LOGGER.trace("tikaContent: {}", tikaContent);

        Pattern pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tikaContent);

        while (matcher.find()) {
          LOGGER.trace("Group count: {}", matcher.groupCount());
          List<String> foundResults = Lists.newArrayList();
          for (int i = 1; i <= matcher.groupCount(); i++) { // skip 0 which is entire match string
            String matchText = matcher.group(i);
            LOGGER.trace("Text: {}", matchText);
            foundResults.add(matchText);
          }
          results.getRecords().add(foundResults);
        }
        return results;
      }
    }
    throw new IllegalStateException("No Tika Feature found");
  }
}
