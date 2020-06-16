package innohack.gem.core.rules;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import innohack.gem.core.feature.AbstractFeature;
import innohack.gem.core.feature.CsvFeature;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CsvHeaderColumnValue extends Rule {

  static final String LABEL = "CSV Header Column Value";
  static final RuleType RULE_TYPE = RuleType.CSV;

  public CsvHeaderColumnValue() {
    this(null);
  }

  public CsvHeaderColumnValue(String value) {
    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(
        Lists.newArrayList(new Parameter("Headers", "aa,bb,cc", ParamType.STRING_LIST, value)));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    Collection<AbstractFeature> abstractFeatureC = gemFile.getData();

    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and csv feature
    while (iterator.hasNext()) {
      AbstractFeature abs = iterator.next();
      if (abs.getClass().getName().equals(CsvFeature.class.getName())) {
        List<Boolean> boolChecker = checkCSVHeader((CsvFeature) abs);
        if (!boolChecker.contains((boolean) false)) {
          return true;
        } else {
          return false;
        }
      }
    }

    return true;
  }

  private List<Boolean> checkCSVHeader(CsvFeature abs) {
    CsvFeature csvFeature = (CsvFeature) abs;
    List<String> csvHeader = csvFeature.getHeaders();
    List<Boolean> counterList = new ArrayList<Boolean>();

    for (Parameter param : getParams()) {
      String[] headerValue = param.getValue().split(",");

      for (String col : headerValue) {

        boolean checkCounter = false;
        for (String csvCol : csvHeader) {
          if (col.trim().toLowerCase().equals(csvCol.trim().toLowerCase())) {
            checkCounter = true;
            break;
          }
        }
        counterList.add(checkCounter);
      }
    }

    return counterList;
  }
}
