package org.elastos.hive.network.request;

import org.elastos.hive.database.FindOptions;
import org.elastos.hive.network.model.KeyValueDict;

public class FindDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict filter;
    private final FindOptions options;

    public FindDocRequestBody(String name, KeyValueDict filter, FindOptions options) {
        super(name);
        this.filter = filter;
        this.options = options;
    }
}
