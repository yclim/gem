package innohack.gem.dao;

import com.google.common.collect.Maps;
import innohack.gem.core.entity.match.MatchFileGroup;
import innohack.gem.core.entity.match.MatchFileRule;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class MatchFileDao implements IMatchFileDao {

  private Map<String, MatchFileGroup> matchFileGroupDb = Maps.newConcurrentMap();
  private Map<String, MatchFileRule> matchFileRuleDb = Maps.newConcurrentMap();

  @Override
  public Map<String, MatchFileGroup> getMatchGroup() {
    return matchFileGroupDb;
  }

  @Override
  public Map<String, MatchFileRule> getMatchRule() {
    return matchFileRuleDb;
  }

  @Override
  public void saveMatchGroup(Map<String, MatchFileGroup> map) {
    matchFileGroupDb.putAll(map);
  }

  @Override
  public void saveMatchRule(Map<String, MatchFileRule> map) {
    matchFileRuleDb.clear();
    matchFileRuleDb.putAll(map);
  }

  @Override
  public void deleteAll() {
    matchFileRuleDb.clear();
    matchFileGroupDb.clear();
  }
}
