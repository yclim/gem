package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.TikaFeature;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TikaMetaValue extends Rule {

  static final String LABEL = "Tika Meta Regex";
  static final RuleType RULE_TYPE = RuleType.TIKA_METADATA;
  static final List<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("headers", "aa,bb,cc", ParamType.STRING_LIST));

  public TikaMetaValue() {
      this(null, null);
  }

  public TikaMetaValue(String key, String value) {
    Parameter param1 = new Parameter("key", "string", ParamType.STRING, key);
    Parameter param2 = new Parameter("value", "string", ParamType.STRING, value);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param1, param2));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    Collection<AbstractFeature> abstractFeatureC = gemFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(TikaFeature.class.getName())) {
        return checkTikaMeta((TikaFeature) abs);
      }
    }

    return false;
  }

  private boolean checkTikaMeta(TikaFeature abs) {
    TikaFeature tikaFeature = (TikaFeature) abs;
    Map<String, String> metadataMap = tikaFeature.getMetadata();
    String keyMatch = "";
    String valueMatch = "";

    for (Parameter param : getParams()) {
      if(param.getLabel().equals("key")) {
        keyMatch = param.getValue();
      }else if (param.getLabel().equals("value")) {
        valueMatch = param.getValue();
      }
    }

    if (metadataMap.containsKey(keyMatch)) {
      String tikaMetadata = metadataMap.get(keyMatch);
      if (tikaMetadata.equals(valueMatch)) {
        return true;
      }
    }
    return false;
  }
}
