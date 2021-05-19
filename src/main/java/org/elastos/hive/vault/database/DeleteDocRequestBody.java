package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class DeleteDocRequestBody extends CreateCollectionRequestBody {
    private KeyValueDict filter;

    public DeleteDocRequestBody(String name, KeyValueDict filter) {
        super(name);
        this.filter = filter;
    }
}
