package innohack.gem.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import innohack.gem.entity.GEMFile;
import java.util.List;

public abstract class GroupExportMixin {

  @JsonIgnore
  abstract int getMatchedCount();

  @JsonIgnore
  abstract List<GEMFile> getMatchedFile();
}
