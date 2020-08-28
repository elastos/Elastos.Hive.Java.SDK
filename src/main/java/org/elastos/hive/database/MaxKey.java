package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonGetter;

public class MaxKey {
	private long value;

	public MaxKey(long value) {
		this.value = value;
	}

	@JsonGetter("$maxKey")
	public long getValue() {
		return value;
	}
}
