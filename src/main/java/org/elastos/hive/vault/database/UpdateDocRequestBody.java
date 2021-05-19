package org.elastos.hive.vault.database;

import org.elastos.hive.network.model.KeyValueDict;

public class UpdateDocRequestBody extends CreateCollectionRequestBody {
    private KeyValueDict filter;
    private KeyValueDict update;
    private UpdateOptions options;

    public UpdateDocRequestBody(String name) {
        super(name);
    }

    public UpdateDocRequestBody setFilter(KeyValueDict filter) {
        this.filter = filter;
        return this;
    }

    public UpdateDocRequestBody setUpdate(KeyValueDict update) {
        this.update = update;
        return this;
    }

    public UpdateDocRequestBody setOptions(UpdateOptions options) {
        this.options = options;
        return this;
    }
}
