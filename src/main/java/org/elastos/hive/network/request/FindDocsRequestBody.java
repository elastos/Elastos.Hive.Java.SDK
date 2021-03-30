package org.elastos.hive.network.request;

import org.elastos.hive.database.FindOptions;
import org.elastos.hive.network.model.KeyValueDict;

public class FindDocsRequestBody extends FindDocRequestBody {
    public FindDocsRequestBody(String name, KeyValueDict filter, FindOptions options) {
        super(name, filter, options);
    }
}
