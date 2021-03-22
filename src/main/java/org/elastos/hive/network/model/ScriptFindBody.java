package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * For the member `condition` and `executable` of the request body.
 */
public class ScriptFindBody {
    @SerializedName("collection")
    private String collection;
    @SerializedName("filter")
    private ScriptKvItem filter;
    @SerializedName("options")
    private ScriptKvItem options;

    public ScriptFindBody(String collection, ScriptKvItem filter) {
        this(collection, filter, null);
    }

    public ScriptFindBody(String collection, ScriptKvItem filter, ScriptKvItem options) {
        this.collection = collection;
        this.filter = filter;
        this.options = options;
    }
}
