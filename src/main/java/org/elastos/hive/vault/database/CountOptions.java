package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.annotations.SerializedName;

/**
 * The request parameter for {@link DatabaseController#countDocuments(String, JsonNode, CountOptions)}
 */
public class CountOptions {
	@SerializedName("skip")
	private Long skip;

	@SerializedName("limit")
	private Long limit;

	@SerializedName("maxTimeMS")
	private Long maxTimeMS;

	public CountOptions setSkip(Long skip) {
		this.skip = skip;
		return this;
	}

	public CountOptions setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public CountOptions setMaxTimeMS(Long maxTimeMS) {
		this.maxTimeMS = maxTimeMS;
		return this;
	}

	public Long getSkip() {
		return skip;
	}

	public Long getLimit() {
		return limit;
	}

	public Long getMaxTimeMS() {
		return maxTimeMS;
	}
}
