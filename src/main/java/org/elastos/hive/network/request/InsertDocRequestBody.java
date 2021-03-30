package org.elastos.hive.network.request;

import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.network.model.KeyValueDict;

public class InsertDocRequestBody extends CreateCollectionRequestBody {
    private final KeyValueDict document;
    private final InsertOptions options;

    public InsertDocRequestBody(String name, KeyValueDict document, InsertOptions options) {
        super(name);
        this.document = document;
        this.options = options;
    }
}
