package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

public class UpdateExecutable extends Executable {
    public UpdateExecutable(String name, String collectionName,
                            KeyValueDict filter, KeyValueDict update, KeyValueDict options) {
        super(name, Type.UPDATE, null);
        super.setBody(new Body(collectionName, filter, update, options));
    }

    public UpdateExecutable(String name, String collectionName,
                            KeyValueDict filter, KeyValueDict update) {
        this(name, collectionName, filter, update, null);
    }

    private class Body extends DatabaseBody {
        @SerializedName("filter")
        private KeyValueDict filter;
        @SerializedName("update")
        private KeyValueDict update;
        @SerializedName("options")
        private KeyValueDict options;

        public Body(String collection, KeyValueDict filter, KeyValueDict update, KeyValueDict options) {
            super(collection);
            this.filter = filter;
            this.update = update;
            this.options = options;
        }
    }
}
