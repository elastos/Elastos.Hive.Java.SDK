package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MaxKey {
	@JsonProperty("$maxKey")
	private long value;

	@JsonCreator
	public MaxKey(@JsonProperty(value = "$maxKey", required = true) long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
