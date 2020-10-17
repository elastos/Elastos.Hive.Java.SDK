package org.elastos.hive.database;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InsertResult extends Result<InsertResult> {
	@JsonProperty("acknowledged")
	private boolean acknowledged;
	@JsonProperty("inserted_id")
	@JsonInclude(Include.NON_NULL)
	private String insertedId;
	@JsonProperty("inserted_ids")
	@JsonInclude(Include.NON_NULL)
	private List<String> insertedIds;

	@JsonCreator
	protected InsertResult() {}

	public boolean acknowledged() {
		return acknowledged;
	}

	public String insertedId() {
		return insertedId;
	}

	public List<String> insertedIds() {
		return insertedIds;
	}

	public static InsertResult deserialize(String content) {
		return deserialize(content, InsertResult.class);
	}
}
