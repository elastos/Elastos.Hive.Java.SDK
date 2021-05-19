package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

public class ScriptUpdateExecutableBody extends ScriptDeleteExecutableBody {

    @SerializedName("update")
    private KeyValueDict update;
    @SerializedName("options")
    private KeyValueDict options;

    @Override
    public ScriptUpdateExecutableBody setCollection(String collection) {
        super.setCollection(collection);
        return this;
    }

    @Override
    public ScriptUpdateExecutableBody setFilter(KeyValueDict filter) {
        super.setFilter(filter);
        return this;
    }

    public ScriptUpdateExecutableBody setUpdate(KeyValueDict update) {
        this.update = update;
        return this;
    }

    public ScriptUpdateExecutableBody setOptions(KeyValueDict options) {
        this.options = options;
        return this;
    }
}
