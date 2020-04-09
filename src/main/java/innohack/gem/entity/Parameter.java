package innohack.gem.entity;

public class Parameter {

	private String label;
	
	private ParamType type;
	
	private String value;

	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ParamType getType() {
		return type;
	}

	public void setType(ParamType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
