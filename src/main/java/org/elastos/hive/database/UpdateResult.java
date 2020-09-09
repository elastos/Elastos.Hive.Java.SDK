package org.elastos.hive.database;

public class UpdateResult extends Result {
	public long matchedCount() {
		return (int)get("matchedCount");
	}

	public long modifiedCount() {
		return (int)get("modifiedCount");
	}

	public long upsertedCount() {
		return (int)get("upsertedCount");
	}

	public String upsertedId() {
		return (String)get("upsertedId");
	}
}
