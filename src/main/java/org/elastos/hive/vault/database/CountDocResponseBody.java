package org.elastos.hive.vault.database;

import org.elastos.hive.connection.HiveResponseBody;

class CountDocResponseBody extends HiveResponseBody {
    private Long count;

    public Long getCount() {
        return this.count;
    }
}
