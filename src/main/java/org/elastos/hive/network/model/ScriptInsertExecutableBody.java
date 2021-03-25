package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class ScriptInsertExecutableBody {
    @SerializedName("collection")
    private String collection;
    @SerializedName("document")
    private ScriptKvItem document;
    @SerializedName("options")
    private ScriptKvItem options;

    public ScriptInsertExecutableBody(String collection, ScriptKvItem document) {
        this(collection, document, null);
    }

    public ScriptInsertExecutableBody(String collection, ScriptKvItem document, ScriptKvItem options) {
        this.collection = collection;
        this.document = document;
        this.options = options;
    }

}
