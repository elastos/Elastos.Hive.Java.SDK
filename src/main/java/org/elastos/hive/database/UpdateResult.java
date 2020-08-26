package org.elastos.hive.database;

public class UpdateResult extends Result {
	public long matchedCount() {
		return (long)get("matchedCount");
	}

	public long modifiedCount() {
		return (long)get("modifiedCount");
	}

	public long upsertedCount() {
		return (long)get("upsertedCount");
	}

	public String upsertedId() {
		return (String)get("upsertedId");
	}
}
