package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Vault script condition to check if a database query returns results or not.
 * This is a way for example to check is a user is in a group, if a message contains comments, if a user
 * is in a list, etc.
 */
public class DbQueryHasResults extends ConditionV2 {
	private static final String TYPE = "queryHasResults";
	private Query query;

	@JsonPropertyOrder({"collection", "filter"})
	public static class Query {
		private String collection;
		private JsonNode filter;

		public Query(String collection, JsonNode query) {
			this.collection = collection;
			this.filter = query;
		}

		@JsonGetter("collection")
		public String getCollection() {
			return collection;
		}

		@JsonGetter("filter")
		public JsonNode getFilter() {
			return filter;
		}
	}

    public DbQueryHasResults(String name, String collection, JsonNode filter) {
    	super(TYPE, name);
        query = new Query(collection, filter);
    }

	@Override
	public Query getBody() {
		return query;
	}
}
