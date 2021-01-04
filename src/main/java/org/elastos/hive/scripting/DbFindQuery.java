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
		private JsonNode options;

		public Query(String collection, JsonNode query) {
			this.collection = collection;
			this.filter = query;
		}

		public Query(String collection, JsonNode query, JsonNode options) {
			this.collection = collection;
			this.filter = query;
			this.options = options;
		}

		@JsonGetter("collection")
		public String getCollection() {
			return collection;
		}

		@JsonGetter("filter")
		public JsonNode getFilter() {
			return filter;
		}

		@JsonGetter("options")
		public JsonNode getOptions() {
			return options;
		}
	}

    public DbFindQuery(String name, String collection, JsonNode filter) {
    	super(TYPE, name);
        query = new Query(collection, filter);
    }

    public DbFindQuery(String name, String collection, JsonNode filter, JsonNode options) {
		super(TYPE, name);
		query = new Query(collection, filter, options);
	}

	public DbFindQuery(String name, String collection, JsonNode filter, JsonNode options, boolean output) {
		super(TYPE, name, output);
		query = new Query(collection, filter, options);
	}

	@Override
	public Query getBody() {
		return query;
	}
}
