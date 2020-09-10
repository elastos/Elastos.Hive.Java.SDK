package org.elastos.hive.database;

public class UpdateResult extends Result {
	public long matchedCount() {
		return (int) (get("matched_count")==null?0:get("matched_count"));
	}

	public long modifiedCount() {
		return (int) (get("modified_count")==null?0:get("modified_count"));
	}

	public long upsertedCount() {
		return (int) (get("upserted_count")==null?0:get("upserted_count"));
	}

	public String upsertedId() {
		return (String) (get("upserted_id")==null?null:get("upserted_id"));
	}
}
