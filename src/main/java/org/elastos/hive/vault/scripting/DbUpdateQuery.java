package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client side representation of a back-end execution that runs a mongo "update" query.
 */
public class DbUpdateQuery extends ExecutableV2 {
	private static final String TYPE = "update";
	private Query query;

	@JsonPropertyOrder({"collection", "filter", "update"})
	public static class Query {
		private String collection;
		private JsonNode filter;
		private JsonNode update;

		public Query(String collection, JsonNode filter, JsonNode update) {
			this.collection = collection;
			this.filter = filter;
			this.update = update;
		}

		@JsonGetter("collection")
		public String getCollection() {
			return collection;
		}

		@JsonGetter("filter")
		public JsonNode getFilter() {
			return filter;
		}

		@JsonGetter("update")
		public JsonNode getUpdate() {
			return update;
		}
	}

    public DbUpdateQuery(String name, String collection, JsonNode filter, JsonNode update) {
    	super(TYPE, name);
        query = new Query(collection, filter, update);
    }

	@Override
	public Query getBody() {
		return query;
	}
}
