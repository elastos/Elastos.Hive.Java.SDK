package org.elastos.hive.vault.database;

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
