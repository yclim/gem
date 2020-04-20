package innohack.gem.service;

import innohack.gem.entity.rule.RuleFactory;
import innohack.gem.entity.rule.RuleType;
import innohack.gem.entity.rule.rules.Rule;
import innohack.gem.service.event.NewEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RuleService extends NewEvent {

  /**
   * List of rules grouped by RuleType; eg CSV, TIKA, EXCEL
   *
   * @return HashMap<RuleType, List<Rule>> Map of lists of rules by RuleType
   */
  public HashMap<RuleType, List<Rule>> getRules() {
    HashMap<RuleType, List<Rule>> mapping = new HashMap<>();
    List<Rule> rules = RuleFactory.createAllInstance();
    for (Rule r : rules) {
      List<Rule> ruleList = mapping.get(r.getRuleType());
      if (ruleList == null) {
        ruleList = new ArrayList<Rule>();
      }
      ruleList.add(r);
      mapping.put(r.getRuleType(), ruleList);
    }
    return mapping;
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
