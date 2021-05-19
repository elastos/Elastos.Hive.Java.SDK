package org.elastos.hive.vault.database;

import org.elastos.hive.network.model.KeyValueDict;

class InsertDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict document;
    private final InsertOneOptions options;

    public InsertDocRequestBody(String name, KeyValueDict document, InsertOneOptions options) {
        super(name);
        this.document = document;
        this.options = options;
    }
}
