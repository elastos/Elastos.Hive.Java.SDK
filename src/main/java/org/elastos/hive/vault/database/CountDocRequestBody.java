package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class CountDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict filter;
    private final CountOptions options;

    public CountDocRequestBody(String name, KeyValueDict filter, CountOptions options) {
        super(name);
        this.filter = filter;
        this.options = options;
    }
}
