package innohack.gem.entity.rule.rules;

import com.google.common.collect.Lists;
import innohack.gem.entity.GEMFile;
import innohack.gem.entity.feature.AbstractFeature;
import innohack.gem.entity.feature.ExcelFeature;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import java.util.*;

public class ExcelHeaderColumnValue extends Rule {

  static final String LABEL = "Excel Header Column Value";
  static final RuleType RULE_TYPE = RuleType.EXCEL;
  static final List<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("headers", "aa,bb,cc", ParamType.STRING_LIST));

  public ExcelHeaderColumnValue() {
    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(PARAMETERS);
  }

  public ExcelHeaderColumnValue(String sheetName, String headers) {
    Parameter param1 = new Parameter("sheetName", "string", ParamType.STRING, sheetName);
    Parameter param2 = new Parameter("headers", "string", ParamType.STRING_LIST, headers);

    this.setLabel(LABEL);
    this.setRuleType(RULE_TYPE);
    this.setParams(Lists.newArrayList(param1, param2));
  }

  @Override
  public boolean check(GEMFile gemFile) {
    Collection<AbstractFeature> abstractFeatureC = gemFile.getData();
    Iterator<AbstractFeature> iterator = abstractFeatureC.iterator();

    // contains both tika and Excel feature
    while (iterator.hasNext()) {
      AbstractFeature feature = iterator.next();
      if (feature.getClass().getName().equals(ExcelFeature.class.getName())) {
        return checkExcelHeader((ExcelFeature) feature);
      }
    }
    return true;
  }

  private Boolean checkExcelHeader(ExcelFeature feature) {
    Map<String, List<List<String>>> dataTable = feature.getSheetTableData();
    String sheetName = getParams().get(0).getValue();
    // check for correct sheet name (exact match)
    if (dataTable.containsKey(sheetName)) {
      String[] headerArr = getParams().get(1).getValue().split(",");
      // get first row to compare the headers
      // can i assume that first line is always not empty?
      List<String> sheetHeader = dataTable.get(sheetName).get(0);
      // check if feature header contains all the input header
      return sheetHeader.containsAll(Arrays.asList(headerArr));
    } else {
      return false;
    }
  }
}
