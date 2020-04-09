package innohack.gem.entity;

import java.util.Collection;

public class Group {
	
	private String id;

	private Collection<Rule> rules;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	public void setRules(Collection<Rule> rules) {
		this.rules = rules;
	}
	
}
