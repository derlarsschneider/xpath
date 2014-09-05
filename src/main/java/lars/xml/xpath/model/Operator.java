package lars.xml.xpath.model;

public enum Operator {
	EQUALS("="), NOT_EQUALS("!=");
	private String value;

	Operator(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Operator fromString(String string) {
		if (string != null) {
			for (Operator operator : Operator.values()) {
				if (string.equals(operator.getValue())) {
					return operator;
				}
			}
		}
		return null;
	}

}
