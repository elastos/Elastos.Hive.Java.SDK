package org.elastos.hive.scripting;

public class SubCondition extends Condition {
	private String conditionName;

	protected SubCondition(String type, String name, String conditionName) {
		super(type, name);
		this.conditionName = conditionName;
	}

	@Override
	public String getBody() {
		return conditionName;
	}
}
