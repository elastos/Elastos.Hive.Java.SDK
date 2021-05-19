package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

public class ScriptDeleteExecutableBody {
    @SerializedName("collection")
    private String collection;
    @SerializedName("filter")
    private KeyValueDict filter;

    public ScriptDeleteExecutableBody setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public ScriptDeleteExecutableBody setFilter(KeyValueDict filter) {
        this.filter = filter;
        return this;
    }
}
