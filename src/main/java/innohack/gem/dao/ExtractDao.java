package innohack.gem.dao;

import com.google.common.collect.Maps;
import innohack.gem.entity.extractor.ExtractConfig;
import innohack.gem.entity.extractor.ExtractedRecords;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ExtractDao implements IExtractDao {
  
  private Map<Integer, ExtractConfig> configs = Maps.newConcurrentMap();
  
  private Map<String, ExtractedRecords> records = Maps.newConcurrentMap();

  @Override
  public ExtractConfig getConfig(int groupId) {
    return configs.get(groupId);
  }

  @Override
  public ExtractConfig saveConfig(int groupId, ExtractConfig config) {
    configs.put(groupId, config);
    return config;
  }

  @Override
  public void saveExtractedRecords(int groupId, String filename, ExtractedRecords results) {
    records.put(toKey(groupId, filename), results);
  }

  @Override
  public ExtractedRecords getExtractedRecords(int groupId, String filename) {
    return records.get(toKey(groupId, filename));
  }
  
  private String toKey(int groupId, String filename) {
    return groupId + "-" + filename;
  }

}
