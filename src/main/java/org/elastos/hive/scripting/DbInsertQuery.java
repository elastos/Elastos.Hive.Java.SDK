package org.elastos.hive.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client side representation of a back-end execution that runs a mongo "insert" query.
 */
public class DbInsertQuery extends Executable {
	private static final String TYPE = "insert";
	private Query query;

	@JsonPropertyOrder({"collection", "document"})
	public static class Query {
		private String collection;
		private JsonNode doc;

		public Query(String collection, JsonNode doc) {
			this.collection = collection;
			this.doc = doc;
		}

		@JsonGetter("collection")
		public String getCollection() {
			return collection;
		}

		@JsonGetter("document")
		public JsonNode getDoc() {
			return doc;
		}
	}

    public DbInsertQuery(String name, String collection, JsonNode doc) {
    	super(TYPE, name);
        query = new Query(collection, doc);
    }

	@Override
	public Query getBody() {
		return query;
	}
}
