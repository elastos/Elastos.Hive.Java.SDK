package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteResult extends Result<DeleteResult> {
	@JsonProperty("deleted_count")
	private int deletedCount;

	public int deletedCount() {
		return deletedCount;
	}

	public static DeleteResult deserialize(String content) {
		return deserialize(content, DeleteResult.class);
	}
}
