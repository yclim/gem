package innohack.gem.entity.rule;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import innohack.gem.entity.GEMFile;

public abstract class GroupExportMixin {

  @JsonIgnore abstract int getMatchedCount();
  
  @JsonIgnore abstract List<GEMFile> getMatchedFile();
  
}
