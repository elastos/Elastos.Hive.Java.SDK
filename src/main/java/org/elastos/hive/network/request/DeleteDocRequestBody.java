package org.elastos.hive.network.request;

import org.elastos.hive.network.model.KeyValueDict;

public class DeleteDocRequestBody extends CreateCollectionRequestBody {
    private KeyValueDict filter;

    public DeleteDocRequestBody(String name, KeyValueDict filter) {
        super(name);
        this.filter = filter;
    }
}
