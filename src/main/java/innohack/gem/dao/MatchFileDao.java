package innohack.gem.dao;

import innohack.gem.database.RocksDatabase;
import innohack.gem.entity.match.MatchFileGroup;
import innohack.gem.entity.match.MatchFileRule;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class MatchFileDao implements IMatchFileDao {

  static final String DB_NAME = "MatchFile";
  RocksDatabase matchFileGroupDb;
  RocksDatabase matchFileRuleDb;

  public MatchFileDao() {
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
      matchFileGroupDb.deleteAll();
    matchFileGroupDb.putHashMap(map);
  }

  @Override
  public void saveMatchRule(Map<String, MatchFileRule> map) {
      matchFileGroupDb.deleteAll();
    matchFileRuleDb.putHashMap(map);
  }
}
