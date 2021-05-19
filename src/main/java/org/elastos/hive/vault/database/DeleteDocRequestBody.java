package org.elastos.hive.vault.database;

import org.elastos.hive.network.model.KeyValueDict;
import org.elastos.hive.vault.database.CreateCollectionRequestBody;

public class DeleteDocRequestBody extends CreateCollectionRequestBody {
    private KeyValueDict filter;

    public DeleteDocRequestBody(String name, KeyValueDict filter) {
        super(name);
        this.filter = filter;
    }
}
