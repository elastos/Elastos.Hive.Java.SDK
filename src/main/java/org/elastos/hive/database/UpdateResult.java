package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class UpdateResult extends Result<UpdateResult> {
	@JsonProperty("matched_count")
	private int matchedCount;
	@JsonProperty("modified_count")
	private int modifiedCount;
	@JsonProperty("upserted_count")
	private int upsertedCount;
	@JsonProperty("upserted_id")
	private String upsertedId;

	public long matchedCount() {
		return matchedCount;
	}

	public long modifiedCount() {
		return modifiedCount;
	}

	public long upsertedCount() {
		return upsertedCount;
	}

	public String upsertedId() {
		return upsertedId;
	}

	public static UpdateResult deserialize(String content) {
		return deserialize(content, UpdateResult.class);
	}
}
