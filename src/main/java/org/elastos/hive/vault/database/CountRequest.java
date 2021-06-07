package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class CountRequest {
    private final KeyValueDict filter;
    private final CountOptions options;

    public CountRequest(KeyValueDict filter, CountOptions options) {
        this.filter = filter;
        this.options = options;
    }
}
