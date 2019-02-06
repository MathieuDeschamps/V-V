package m2.model;

public class ParameterModel {
	
	private String type;
	private String value;
	public ParameterModel() {
		super();
		this.type = "any";
		this.value = "unknown";
	}
		
	public ParameterModel(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ParameterModel)) {
			return false;
		}
		ParameterModel pm = (ParameterModel) o;
		return type.equals(pm.type);
	}
}
