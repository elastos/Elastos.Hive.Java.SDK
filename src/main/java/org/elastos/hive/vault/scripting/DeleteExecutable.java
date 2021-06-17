package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

public class DeleteExecutable extends Executable {
    public DeleteExecutable(String name, String collectionName, KeyValueDict filter) {
        super(name, Type.UPDATE, null);
        super.setBody(new Body(collectionName, filter));
    }

    public DeleteExecutable(String name, String collectionName) {
        this(name, collectionName, null);
    }

    private class Body extends DatabaseBody {
        @SerializedName("filter")
        private KeyValueDict filter;

        public Body(String collection, KeyValueDict filter) {
            super(collection);
            this.filter = filter;
        }
    }
}
