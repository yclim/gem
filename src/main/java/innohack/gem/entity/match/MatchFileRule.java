package innohack.gem.entity.match;

import innohack.gem.entity.GEMFile;
import java.util.HashMap;

public class MatchFileRule extends GEMFile {
  private HashMap<Integer, Boolean> matchRuleHashcode;

  public MatchFileRule() {
    matchRuleHashcode = new HashMap<Integer, Boolean>();
  }

  public HashMap<Integer, Boolean> getMatchRuleHashcode() {
    return matchRuleHashcode;
  }

  public void setMatchRuleHashcode(HashMap<Integer, Boolean> matchRuleHashcode) {
    this.matchRuleHashcode = matchRuleHashcode;
  }
}
