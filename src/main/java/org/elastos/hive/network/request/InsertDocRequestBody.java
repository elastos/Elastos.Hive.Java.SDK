package org.elastos.hive.network.request;

import org.elastos.hive.database.InsertOneOptions;
import org.elastos.hive.network.model.KeyValueDict;

public class InsertDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict document;
    private final InsertOneOptions options;

    public InsertDocRequestBody(String name, KeyValueDict document, InsertOneOptions options) {
        super(name);
        this.document = document;
        this.options = options;
    }
}
