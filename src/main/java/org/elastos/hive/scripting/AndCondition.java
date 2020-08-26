package org.elastos.hive.scripting;

/**
 * Vault script condition that succeeds only if all the contained conditions are successful.
 */
public class AndCondition extends AggregatedCondition {
	private static final String TYPE = "and";

	public AndCondition(String name, Condition[] conditions) {
		super(TYPE, name, conditions);
	}

	public AndCondition(String name) {
		this(name, null);
	}
}
