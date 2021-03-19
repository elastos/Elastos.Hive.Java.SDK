package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class Executable {
	@SerializedName("name")
	protected String name;
	@SerializedName("type")
	protected String type;
	@SerializedName("body")
	protected Object body;

	public Executable setName(String name) {
		this.name = name;
		return this;
	}

	public Executable setType(String type) {
		this.type = type;
		return this;
	}

	public Executable setBody(Object body) {
		this.body = body;
		return this;
	}
}
