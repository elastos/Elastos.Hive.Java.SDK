package org.elastos.hive.database;

public enum ReadConcern {
	LOCAL, AVAILABLE, MAJORITY, LINEARIZABLE, SNAPSHOT;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
