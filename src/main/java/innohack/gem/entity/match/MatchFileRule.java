package innohack.gem.entity.match;

import java.util.HashMap;

public class MatchFileRule {
  private String filePath;
  private HashMap<Integer, Boolean> matchRuleHashcode;

  public MatchFileRule() {
    matchRuleHashcode = new HashMap<Integer, Boolean>();
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public HashMap<Integer, Boolean> getMatchRuleHashcode() {
    return matchRuleHashcode;
  }

  public void setMatchRuleHashcode(HashMap<Integer, Boolean> matchRuleHashcode) {
    this.matchRuleHashcode = matchRuleHashcode;
  }
}
