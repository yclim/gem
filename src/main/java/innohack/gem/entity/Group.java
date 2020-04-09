package innohack.gem.entity;

import java.util.Collection;

public class Group {
	
	private String groupName;

	private Collection<Rule> rules;
	

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	public void setRules(Collection<Rule> rules) {
		this.rules = rules;
	}
	
}