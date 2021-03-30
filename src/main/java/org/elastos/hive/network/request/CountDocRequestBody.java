package org.elastos.hive.network.request;

import org.elastos.hive.database.CountOptions;
import org.elastos.hive.network.model.KeyValueDict;

public class CountDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict filter;
    private final CountOptions options;

    public CountDocRequestBody(String name, KeyValueDict filter, CountOptions options) {
        super(name);
        this.filter = filter;
        this.options = options;
    }
}
