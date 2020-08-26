package org.elastos.hive.database;

public class DeleteResult extends Result {
	public long deletedCount() {
		return (long)get("deletedCount");
	}
}
