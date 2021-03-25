package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class ScriptDeleteExecutableBody {
    @SerializedName("collection")
    private String collection;
    @SerializedName("filter")
    private ScriptKvItem filter;

    public ScriptDeleteExecutableBody setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public ScriptDeleteExecutableBody setFilter(ScriptKvItem filter) {
        this.filter = filter;
        return this;
    }
}
