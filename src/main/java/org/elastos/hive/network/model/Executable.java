package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class Executable extends Condition {
	public static final String TYPE_FIND = "find";
	public static final String TYPE_FILE_UPLOAD = "fileUpload";

	@SerializedName("output")
	private Boolean output;

	public Executable(String name, String type, Object body) {
		super(name, type, body);
	}

	public Executable setOutput(boolean output) {
		this.output = output;
		return this;
	}
}
