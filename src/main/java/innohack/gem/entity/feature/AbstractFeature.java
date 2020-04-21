package innohack.gem.entity.feature;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CsvFeature.class),
  @JsonSubTypes.Type(value = TikaFeature.class),
  @JsonSubTypes.Type(value = ExcelFeature.class)
})
public abstract class AbstractFeature {
  private Map<String, String> metadata = null;

  public Map<String, String> getMetadata() {
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

  public void printMetadata() {
    for (Map.Entry<String, String> entry : metadata.entrySet()) {
      System.out.println(entry.getKey() + " = " + entry.getValue());
    }
  }

  public abstract void extract(File f) throws Exception;
}
