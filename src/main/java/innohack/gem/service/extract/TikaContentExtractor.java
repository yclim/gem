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

public class TikaContentExtractor extends AbstractExtractor {

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

        Pattern pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tikaContent);
        int count = 0;

        List<String> foundResults = Lists.newArrayList();
        while (matcher.find()) {
          count++;
          String matchText = tikaContent.substring(matcher.start(), matcher.end());
          foundResults.add(matchText);
          results.getRecords().add(foundResults);
        }

        return results;
      }
    }
    throw new IllegalStateException("No Tika Feature found");
  }
}
