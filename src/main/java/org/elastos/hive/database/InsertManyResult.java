package org.elastos.hive.database;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class InsertManyResult extends Result<InsertManyResult> {
	@JsonProperty("acknowledged")
	private boolean acknowledged;
	@JsonProperty("inserted_ids")
	@JsonInclude(Include.NON_NULL)
	private List<String> insertedIds;

	@JsonCreator
	protected InsertManyResult() {}

	public boolean acknowledged() {
		return acknowledged;
	}

	public List<String> insertedIds() {
		return insertedIds;
	}

	public static InsertManyResult deserialize(String content) {
		return deserialize(content, InsertManyResult.class);
	}
}
