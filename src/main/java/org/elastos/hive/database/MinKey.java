package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonGetter;

public class MinKey {
	private long value;

	public MinKey(long value) {
		this.value = value;
	}

	@JsonGetter("$minKey")
	public long getValue() {
		return value;
	}
}
