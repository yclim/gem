package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TikaContentRegex extends Rule {

  static final String LABEL = "Tika Content Regex";
  static final RuleType RULE_TYPE = RuleType.TIKA_CONTENT;

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
    Collection<AbstractFeature> abstractFeatureC = gemFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(TikaFeature.class.getName())) {
        return checkTikaContent((TikaFeature) abs);
      }
    }

    return false;
    // LOGGER.debug("File Extension {} rule: {}", ext, gemFile.getExtension());
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

      List<String> result = new ArrayList<String>();

      while (matcher.find()) {
        String matchString = matcher.group(1);
        System.out.println("The  match string is " + matchString);
        return true;
      }
    }
    return false;
  }
}
