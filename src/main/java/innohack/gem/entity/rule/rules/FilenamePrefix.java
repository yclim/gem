package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import innohack.gem.util.Util;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilenamePrefix extends Rule {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilenamePrefix.class);

  static final String LABEL = "Filename Prefix";
  static final RuleType RULE_TYPE = RuleType.FILE;
  static final List<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("prefix", "string", ParamType.STRING));

  public FilenamePrefix() {
    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(PARAMETERS);
  }

  public FilenamePrefix(String value) {
    Parameter param = new Parameter("prefix", "string", ParamType.STRING, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    final String prefix = Util.first(getParams()).getValue();
    LOGGER.debug(
        "{} {} rule: {}", FilenamePrefix.class.getSimpleName(), prefix, gemFile.getExtension());
    return gemFile.getFileName().startsWith(prefix);
  }
}
