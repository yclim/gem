package innohack.gem.dao;

import innohack.gem.core.entity.match.MatchFileGroup;
import innohack.gem.core.entity.match.MatchFileRule;
import innohack.gem.database.RocksDatabase;
import java.util.Map;

public class MatchFileRockDao implements IMatchFileDao {

  private static final String DB_NAME = "MatchFile";
  private RocksDatabase<String, MatchFileGroup> matchFileGroupDb;
  private RocksDatabase<String, MatchFileRule> matchFileRuleDb;

  public MatchFileRockDao() {
    matchFileGroupDb =
        RocksDatabase.getInstance(DB_NAME + "_Group", String.class, MatchFileGroup.class);
    matchFileRuleDb =
        RocksDatabase.getInstance(DB_NAME + "_Rule", String.class, MatchFileRule.class);
  }

  @Override
  public Map<String, MatchFileGroup> getMatchGroup() {
    return matchFileGroupDb.getKeyValues();
  }

  @Override
  public Map<String, MatchFileRule> getMatchRule() {
    return matchFileRuleDb.getKeyValues();
  }

  @Override
  public void saveMatchGroup(Map<String, MatchFileGroup> map) {
    matchFileGroupDb.putHashMap(map, true);
  }

  @Override
  public void saveMatchRule(Map<String, MatchFileRule> map) {
    matchFileRuleDb.deleteAll();
    matchFileRuleDb.putHashMap(map, true);
  }

  @Override
  public void deleteAll() {
    matchFileRuleDb.deleteAll();
    matchFileGroupDb.deleteAll();
  }
}
