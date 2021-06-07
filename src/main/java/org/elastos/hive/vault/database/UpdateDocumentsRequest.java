package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class UpdateDocumentsRequest {
    private KeyValueDict filter;
    private KeyValueDict update;
    private UpdateDocumentsOptions options;

    public UpdateDocumentsRequest setFilter(KeyValueDict filter) {
        this.filter = filter;
        return this;
    }

    public UpdateDocumentsRequest setUpdate(KeyValueDict update) {
        this.update = update;
        return this;
    }

    public UpdateDocumentsRequest setOptions(UpdateDocumentsOptions options) {
        this.options = options;
        return this;
    }
}
