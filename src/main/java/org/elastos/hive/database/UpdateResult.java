package org.elastos.hive.database;

public interface UpdateResult {
	public long matchedCount();
	public long modifiedCount();
	public long upsertedCount();
	public String upsertedId();
}
