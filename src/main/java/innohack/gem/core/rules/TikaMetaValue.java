package innohack.gem.core.rules;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import innohack.gem.core.feature.TikaFeature;
import java.util.Map;

public class TikaMetaValue extends Rule {

  static final String LABEL = "Tika Meta Value";
  static final RuleType RULE_TYPE = RuleType.TIKA_METADATA;

  public TikaMetaValue() {
    this(null, null);
  }

  public TikaMetaValue(String key, String value) {
    Parameter param1 = new Parameter("Key", "Content-Type", ParamType.STRING, key);
    Parameter param2 = new Parameter("Value", "application/pdf", ParamType.STRING, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param1, param2));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    return gemFile.getData().stream()
        .filter(feature -> feature instanceof TikaFeature)
        .map(feature -> (TikaFeature) feature)
        .anyMatch(tikaFeature -> checkTikaMeta(tikaFeature));
  }

  private boolean checkTikaMeta(TikaFeature feature) {
    Map<String, String> metadataMap = feature.getMetadata();
    String key = getParams().get(0).getValue();
    String value = getParams().get(1).getValue();
    if (metadataMap.containsKey(key) && metadataMap.get(key).equals(value)) {
      return true;
    }
    return false;
  }
}
