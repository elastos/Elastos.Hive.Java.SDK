package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class CountDocumentRequest {
    private final KeyValueDict filter;
    private final CountDocumentOptions options;

    public CountDocumentRequest(KeyValueDict filter, CountDocumentOptions options) {
        this.filter = filter;
        this.options = options;
    }
}
