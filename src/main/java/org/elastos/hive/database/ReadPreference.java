package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReadPreference {
	PRIMARY, PRIMARY_PREFERRED, SECONDARY, SECONDARY_PREFERRED, NEAREST;

	@Override
	@JsonValue
	public String toString() {
		return name();
	}

	@JsonCreator
	public static ReadPreference fromString(String name) {
		return valueOf(name);
	}
}
