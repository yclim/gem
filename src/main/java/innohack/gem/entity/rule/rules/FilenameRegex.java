package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import innohack.gem.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilenameRegex extends Rule {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilenameRegex.class);

  static final String LABEL = "Filename Regex";
  static final RuleType RULE_TYPE = RuleType.FILE;

  public FilenameRegex() {
    this(null);
  }

  public FilenameRegex(String value) {
    Parameter param = new Parameter("Regex", ".*\\s+[0-9]{3}.*", ParamType.REGEX, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    final String filenameRegex = Util.first(getParams()).getValue();

    Pattern pattern = Pattern.compile(filenameRegex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(gemFile.getFileName());

    while (matcher.find()) {
      String matchString = matcher.group();
      LOGGER.debug("The  match string is " + matchString);
      return true;
    }
    return false;
  }
}
