package org.elastos.hive.database;

public enum ReadPreference {
	PRIMARY, PRIMARY_PREFERRED, SECONDARY, SECONDARY_PREFERRED, NEAREST;

	@Override
	public String toString() {
		return name();
	}
}
