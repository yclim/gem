package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import java.util.Collection;

public class FileExtension extends Rule {

  static final String LABEL = "File Extension";
  static final RuleType RULE_TYPE = RuleType.FILE;
  static final Collection<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("extension", "string", ParamType.STRING));

  public FileExtension() {
    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(PARAMETERS);
  }

  @Override
  public boolean check(GEMFile gemFile) {
    return true;
  }
}
