package innohack.gem.entity.gem.data;

import java.io.File;
import java.util.HashMap;

public abstract class AbstractFeature {
  private HashMap<String, String> metadata = null;
  private Target target;

  AbstractFeature(Target target) {
    this.target = target;
  }

  AbstractFeature(Target target, HashMap<String, String> metadata) {
    this.target = target;
    this.metadata = metadata;
  }

  public Target getTarget() {
    return target;
  }

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

  public void setTarget(Target target) {
    this.target = target;
  }

  public abstract void extract(File f) throws Exception;
}
