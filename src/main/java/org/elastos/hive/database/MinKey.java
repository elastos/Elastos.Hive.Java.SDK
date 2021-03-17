package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MinKey {
	@JsonProperty("$minKey")
	private long value;

	@JsonCreator
	public MinKey(@JsonProperty(value = "$minKey", required = true) long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
