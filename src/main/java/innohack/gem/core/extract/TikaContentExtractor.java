package innohack.gem.core.extract;

import com.beust.jcommander.internal.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedRecords;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.feature.AbstractFeature;
import innohack.gem.core.feature.TikaFeature;
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
  private String LABEL = "Tika Content Regex Extractor";
  private static final Logger LOGGER = LoggerFactory.getLogger(TikaContentExtractor.class);

  public TikaContentExtractor() {
    this(null);
  }

  public TikaContentExtractor(String regex) {
    setLabel(LABEL);
    setParams(Lists.newArrayList(new Parameter("Content", "", ParamType.STRING, regex)));
  }

  @Override
  public ExtractedRecords extract(GEMFile f, ExtractConfig extractConfig) throws Exception {

    for (AbstractFeature feature : f.getData()) {
      if (feature instanceof TikaFeature) {
        String regexString = getParams().get(0).getValue();

        ExtractedRecords results = new ExtractedRecords();
        String tikaContent = ((TikaFeature) feature).getContent();
        LOGGER.trace("tikaContent: {}", tikaContent);

        Pattern pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tikaContent);

        while (matcher.find()) {
          LOGGER.trace("Group count: {}, {}", matcher.groupCount(), matcher.group(0));
          List<String> foundResults = Lists.newArrayList();
          for (int i = 1; i <= matcher.groupCount(); i++) { // skip 0 which is entire match string
            String matchText = matcher.group(i);
            LOGGER.trace("Text: {}", matchText);
            foundResults.add(matchText);
          }
          if (matcher.groupCount() == 0) foundResults.add(matcher.group(0));
          if (!foundResults.isEmpty()) results.getRecords().add(foundResults);
        }
        return results;
      }
    }
    throw new IllegalStateException("No Tika Feature found");
  }
}
