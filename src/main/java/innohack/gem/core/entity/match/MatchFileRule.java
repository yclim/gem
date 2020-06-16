package innohack.gem.core.entity.match;

import innohack.gem.core.entity.GEMFile;
import java.util.HashMap;

public class MatchFileRule extends GEMFile {

  // map rule's hashcode to boolean (check() result)
  private HashMap<Integer, Boolean> ruleResultMap;

  public MatchFileRule() {
    ruleResultMap = new HashMap<Integer, Boolean>();
  }

  public HashMap<Integer, Boolean> getRuleResultMap() {
    return ruleResultMap;
  }

  public void setRuleResultMap(HashMap<Integer, Boolean> ruleResultMap) {
    this.ruleResultMap = ruleResultMap;
  }
}
