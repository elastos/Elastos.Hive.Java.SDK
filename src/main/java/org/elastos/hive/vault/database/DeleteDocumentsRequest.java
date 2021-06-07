package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class DeleteDocumentsRequest {
    private KeyValueDict filter;

    public DeleteDocumentsRequest(KeyValueDict filter) {
        this.filter = filter;
    }
}
