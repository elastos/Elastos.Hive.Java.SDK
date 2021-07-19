package org.elastos.hive.vault.database;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The request parameter for {@link DatabaseController#find(String, JsonNode, FindOptions)}
 */
public class FindOptions {
	private Integer skip;
	private Integer limit;

	public FindOptions setSkip(Integer skip) {
		this.skip = skip;
		return this;
	}

	public FindOptions setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public Integer getSkip() {
		return skip;
	}

	public String getSkipStr() {
		return skip != null ? String.valueOf(skip) : "";
	}

	public Integer getLimit() {
		return limit;
	}

	public String getLimitStr() {
		return limit != null ? String.valueOf(limit) : "";
	}
}
