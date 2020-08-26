package org.elastos.hive.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client side representation of a back-end execution that runs a mongo "find" query and returns some items
 * as a result.
 */
public class DbFindQuery extends Executable {
	private static final String TYPE = "find";
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

    public DbFindQuery(String name, String collection, JsonNode filter) {
    	super(TYPE, name);
        query = new Query(collection, filter);
    }

	@Override
	public Query getBody() {
		return query;
	}
}
