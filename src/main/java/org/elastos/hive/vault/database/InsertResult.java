package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * The request result for {@link DatabaseController#insertOne(String, JsonNode, InsertOptions)}
 */
public class InsertResult {
	@SerializedName("acknowledged")
	private Boolean acknowledged;

	@SerializedName("inserted_ids")
	private List<String> insertedIds;

	public Boolean getAcknowledged() {
		return acknowledged;
	}

	public List<String> getInsertedIds() {
		return insertedIds;
	}
}
