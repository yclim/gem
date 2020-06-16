package innohack.gem.core.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.feature.TikaFeature;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TikaContentRegex extends Rule {

  private static final Logger LOGGER = LoggerFactory.getLogger(TikaContentRegex.class);

  private static final String LABEL = "Tika Content Regex";
  private static final RuleType RULE_TYPE = RuleType.TIKA_CONTENT;

  public TikaContentRegex() {
    this(null);
  }

  public TikaContentRegex(String value) {
    Parameter param = new Parameter("Regex", ".*\\s+[0-9]{3}.*", ParamType.REGEX, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    return gemFile.getData().stream()
        .filter(feature -> feature instanceof TikaFeature)
        .map(feature -> (TikaFeature) feature)
        .anyMatch(tikaFeature -> checkTikaContent(tikaFeature));
  }

  private boolean checkTikaContent(TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;
    String tikaContent = tikaFeature.getContent();

    for (Parameter param : getParams()) {
      String regexString = param.getValue();
      // in case you would like to ignore case sensitivity,
      // you could use this statement:
      // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);

      Pattern pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(tikaContent);

      if (matcher.find()) {
        return true;
      }
    }
    return false;
  }
}
