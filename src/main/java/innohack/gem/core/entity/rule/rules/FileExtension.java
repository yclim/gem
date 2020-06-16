package innohack.gem.core.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import innohack.gem.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileExtension extends Rule {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileExtension.class);

  private static final String LABEL = "File Extension";
  private static final RuleType RULE_TYPE = RuleType.FILE;

  public FileExtension() {
    this(null);
  }

  public FileExtension(String value) {
    Parameter param = new Parameter("File extension", "txt", ParamType.STRING, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    final String ext = Util.first(getParams()).getValue();
    return ext.equalsIgnoreCase(gemFile.getExtension());
  }
}
