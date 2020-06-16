package innohack.gem.dao;

import innohack.gem.core.entity.extract.ExtractConfig;
import innohack.gem.core.entity.extract.ExtractedRecords;

public interface IExtractDao {

  ExtractConfig getConfig(int groupId);

  ExtractConfig saveConfig(int groupId, ExtractConfig config);

  void saveExtractedRecords(int groupId, String filename, ExtractedRecords results);

  ExtractedRecords getExtractedRecords(int groupId, String filename);
}
