package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReadConcern {
	LOCAL, AVAILABLE, MAJORITY, LINEARIZABLE, SNAPSHOT;

	@Override
	@JsonValue
	public String toString() {
		return name().toLowerCase();
	}

	@JsonCreator
	public static ReadConcern fromString(String name) {
		return valueOf(name.toUpperCase());
	}
}
