package org.elastos.hive.vault.scripting;

/**
 * Vault script condition that succeeds if at least one of the contained conditions are successful.
 * Contained conditions are tested in the given order, and test stops as soon as one successful condition
 * succeeds.
 */
public class OrCondition extends AggregatedCondition {
	private static final String TYPE = "or";

	public OrCondition(String name, Condition[] conditions) {
		super(TYPE, name, conditions);
	}

	public OrCondition(String name) {
		this(name, null);
	}
}
