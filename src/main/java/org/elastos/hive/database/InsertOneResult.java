package org.elastos.hive.database;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InsertOneResult extends Result<InsertOneResult> {
	@JsonProperty("acknowledged")
	private boolean acknowledged;
	@JsonProperty("inserted_id")
	@JsonInclude(Include.NON_NULL)
	private String insertedId;

	@JsonCreator
	protected InsertOneResult() {}

	public boolean acknowledged() {
		return acknowledged;
	}

	public String insertedId() {
		return insertedId;
	}

	public static InsertOneResult deserialize(String content) {
		return deserialize(content, InsertOneResult.class);
	}
}
