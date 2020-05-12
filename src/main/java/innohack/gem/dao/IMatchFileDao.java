package innohack.gem.dao;

import innohack.gem.entity.match.MatchFileGroup;
import innohack.gem.entity.match.MatchFileRule;
import java.util.Map;

public interface IMatchFileDao {

  /*
  retrieve
   */
  abstract Map<String, MatchFileGroup> getMatchGroup();

  abstract Map<String, MatchFileRule> getMatchRule();

  /*
  save
   */
  abstract void saveMatchGroup(Map<String, MatchFileGroup> map);

  abstract void saveMatchRule(Map<String, MatchFileRule> map);

  abstract void deleteAll();
}
