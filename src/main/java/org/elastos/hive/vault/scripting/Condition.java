package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

/**
 * The condition is used for registering the script.
 * If the condition matches, the script will be executed normally.
 */
public abstract class Condition {
	@SerializedName("name")
	private String name;
	@SerializedName("type")
	private String type;
	@SerializedName("body")
	private Object body;

	protected Condition(String name, String type, Object body) {
		this.name = name;
		this.type = type;
		this.body = body;
	}

	protected void setBody(Object value) {
		body = value;
	}

	protected Object getBody() {
		return body;
	}
}
