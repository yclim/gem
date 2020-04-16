package innohack.gem.entity.feature;

import java.io.File;
import java.util.HashMap;

public abstract class AbstractFeature {
  private HashMap<String, String> metadata = null;

  public HashMap<String, String> getMetadata() {
    return metadata;
  }

  public void addMetadata(String key, String value) {
    if (metadata == null) {
      metadata = new HashMap<>();
    }
    metadata.put(key, value);
  }

  public void removeMetadata(String key) {
    if (metadata != null) {
      metadata.remove(key);
    }
  }

  public abstract void extract(File f) throws Exception;
}
