package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

class QueryDocumentsRequest {
    @SerializedName("collection")
    private String collectionName;
    private KeyValueDict filter;
    private QueryDocumentsOptions options;

    public QueryDocumentsRequest setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public QueryDocumentsRequest setFilter(KeyValueDict filter) {
        this.filter = filter;
        return this;
    }

    public QueryDocumentsRequest setOptions(QueryDocumentsOptions options) {
        this.options = options;
        return this;
    }
}
