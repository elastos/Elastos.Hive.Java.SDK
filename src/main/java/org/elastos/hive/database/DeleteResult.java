package org.elastos.hive.database;

public class DeleteResult extends Result {
	public int deletedCount() {
		return (int)get("deleted_count");
	}
}
