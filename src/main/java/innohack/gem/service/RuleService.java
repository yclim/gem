package innohack.gem.service;

import innohack.gem.core.entity.rule.RuleFactory;
import innohack.gem.core.entity.rule.rules.Rule;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

  /**
   * List of rules grouped by RuleType; eg CSV, TIKA, EXCEL
   *
   * @return HashMap<RuleType, List<Rule>> Map of lists of rules by RuleType
   */
  public List<Rule> getRules() {
    return RuleFactory.createAllInstance();
  }
  /**
   * Get a rule by ruleId
   *
   * @return Rule {@ruleId String }
   */
  public Rule getRule(String ruleId) {
    return RuleFactory.createInstance(ruleId);
  }
}
