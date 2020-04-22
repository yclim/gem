package innohack.gem.entity.rule.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import innohack.gem.entity.GEMFile;
import innohack.gem.entity.rule.ParamType;
import innohack.gem.entity.rule.Parameter;
import innohack.gem.entity.rule.RuleType;
import innohack.gem.util.Util;

public class FileExtension extends Rule {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(FileExtension.class);

  private static final String LABEL = "File Extension";
  private static final RuleType RULE_TYPE = RuleType.FILE;
  
  public FileExtension() {
    this(null);
  }
  
  public FileExtension(String value) {
	  Parameter param = new Parameter("extension", "string", ParamType.STRING, value);
		
	  this.setLabel(LABEL);
	  this.setRuleType(RULE_TYPE);
	  this.setParams(Lists.newArrayList(param));
  }

  @Override
  public boolean check(GEMFile gemFile) {
	  final String ext = Util.first(getParams()).getValue();
	  LOGGER.debug("File Extension {} rule: {}", ext, gemFile.getExtension());
	  return ext.equalsIgnoreCase(gemFile.getExtension());
  }
  
}
