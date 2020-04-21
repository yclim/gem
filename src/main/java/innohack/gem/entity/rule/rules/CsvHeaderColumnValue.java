package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import java.util.Collection;

public class CsvHeaderColumnValue extends Rule {

  static final String LABEL = "CSV Header Column Value";
  static final RuleType RULE_TYPE = RuleType.CSV;
  static final Collection<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("headers", "aa,bb,cc", ParamType.STRING_LIST));

  public CsvHeaderColumnValue() {
    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(PARAMETERS);
  }

  @Override
  public boolean check(GEMFile gemFile) {
    return true;
  }
}
