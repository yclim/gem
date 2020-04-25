package innohack.gem.web;

import innohack.gem.entity.rule.rules.Rule;
import innohack.gem.service.RuleService;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

  @Autowired private RuleService ruleService;

  /**
   * List of rules grouped by RuleType; eg CSV, TIKA, EXCEL
   *
   * @return Map of lists of rules by RuleType {@link HashMap < RuleType , List < Rule >>}
   */
  @GetMapping("/list")
  public List<Rule> getRules() {
    return ruleService.getRules();
  }

  /**
   * Get a rule by ruleId
   *
   * @return Rule {@ruleId String }
   */
  @GetMapping
  public Rule getRule(@RequestParam(name = "ruleId") String ruleId) {
    return ruleService.getRule(ruleId);
  }
}
