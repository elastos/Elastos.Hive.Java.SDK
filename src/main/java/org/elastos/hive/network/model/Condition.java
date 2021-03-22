package org.elastos.hive.network.model;

public class Condition extends Executable {
	public static final String TYPE_AND = "and";
	public static final String TYPE_OR = "or";

	public Condition(String name, String type, Object body) {
		super(name, type, body);
	}
}
