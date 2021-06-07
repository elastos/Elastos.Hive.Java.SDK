package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class DeleteRequest {
    private KeyValueDict filter;

    public DeleteRequest(KeyValueDict filter) {
        this.filter = filter;
    }
}
