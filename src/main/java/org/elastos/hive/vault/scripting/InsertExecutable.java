package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

public class InsertExecutable extends Executable {
    public InsertExecutable(String name, String collectionName, KeyValueDict document, KeyValueDict options) {
        super(name, Type.INSERT, null);
        super.setBody(new Body(collectionName, document, options));
    }

    public InsertExecutable(String name, String collectionName, KeyValueDict document) {
        this(name, collectionName, document, null);
    }

    private class Body extends DatabaseBody {
        @SerializedName("document")
        private KeyValueDict document;
        @SerializedName("options")
        private KeyValueDict options;

        public Body(String collection, KeyValueDict document, KeyValueDict options) {
            super(collection);
            this.document = document;
            this.options = options;
        }
    }
}
