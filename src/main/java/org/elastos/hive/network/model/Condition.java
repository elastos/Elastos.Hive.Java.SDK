package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class Condition {
	public static final String TYPE_AND = "and";
	public static final String TYPE_OR = "or";

	@SerializedName("name")
	private String name;
	@SerializedName("type")
	private String type;
	@SerializedName("body")
	private Object body;

	public Condition(String name, String type, Object body) {
		this.name = name;
		this.type = type;
		this.body = body;
	}
}
