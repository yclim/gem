package innohack.gem.core.rules;

import com.google.common.collect.Lists;
import innohack.gem.core.entity.GEMFile;
import innohack.gem.core.entity.rule.ParamType;
import innohack.gem.core.entity.rule.Parameter;
import innohack.gem.core.entity.rule.RuleType;
import innohack.gem.core.feature.AbstractFeature;
import innohack.gem.core.feature.ExcelFeature;
import java.util.*;

public class ExcelHeaderColumnValue extends Rule {

  static final String LABEL = "Excel Header Column Value";
  static final RuleType RULE_TYPE = RuleType.EXCEL;
  static final List<Parameter> PARAMETERS =
      Lists.newArrayList(new Parameter("Headers", "aa,bb,cc", ParamType.STRING_LIST));

  public ExcelHeaderColumnValue() {
    this(null, null);
  }

  public ExcelHeaderColumnValue(String sheetName, String headers) {
    Parameter param1 = new Parameter("Sheet Name", "sheet1", ParamType.STRING, sheetName);
    Parameter param2 = new Parameter("Headers", "aa,bb,cc", ParamType.STRING_LIST, headers);

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
