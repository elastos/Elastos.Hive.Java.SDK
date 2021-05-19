package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;

class FindDocsRequestBody extends FindDocRequestBody {
    public FindDocsRequestBody(String name, KeyValueDict filter, FindOptions options) {
        super(name, filter, options);
    }
}
