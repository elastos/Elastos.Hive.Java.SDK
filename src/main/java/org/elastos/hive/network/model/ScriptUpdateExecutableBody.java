package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class ScriptUpdateExecutableBody extends ScriptDeleteExecutableBody {

    @SerializedName("update")
    private ScriptKvItem update;
    @SerializedName("options")
    private ScriptKvItem options;

    @Override
    public ScriptUpdateExecutableBody setCollection(String collection) {
        super.setCollection(collection);
        return this;
    }

    @Override
    public ScriptUpdateExecutableBody setFilter(ScriptKvItem filter) {
        super.setFilter(filter);
        return this;
    }

    public ScriptUpdateExecutableBody setUpdate(ScriptKvItem update) {
        this.update = update;
        return this;
    }

    public ScriptUpdateExecutableBody setOptions(ScriptKvItem options) {
        this.options = options;
        return this;
    }
}
