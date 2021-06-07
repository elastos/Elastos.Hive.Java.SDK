package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

class QueryRequest {
    @SerializedName("collection")
    private String collectionName;
    private KeyValueDict filter;
    private QueryOptions options;

    public QueryRequest setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public QueryRequest setFilter(KeyValueDict filter) {
        this.filter = filter;
        return this;
    }

    public QueryRequest setOptions(QueryOptions options) {
        this.options = options;
        return this;
    }
}
