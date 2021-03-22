package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class Executable {
	public static final String TYPE_FIND = "find";

	@SerializedName("name")
	private String name;
	@SerializedName("type")
	private String type;
	@SerializedName("body")
	private Object body;

	public Executable(String name, String type, Object body) {
		this.name = name;
		this.type = type;
		this.body = body;
	}

}
