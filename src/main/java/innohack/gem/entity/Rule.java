package innohack.gem.entity;

import java.util.Collection;

public class Rule {

	private String ruleId;
	
    private String label;
    
    private Collection<Parameter> params;
    
    private String target; //XXX not sure how this is applied...

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Collection<Parameter> getParams() {
		return params;
	}

	public void setParams(Collection<Parameter> params) {
		this.params = params;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
