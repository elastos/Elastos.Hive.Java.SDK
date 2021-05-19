package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.KeyValueDict;

import java.util.List;

class InsertDocsRequestBody extends CreateCollectionRequestBody {
    @SerializedName("document")
    private final List<KeyValueDict> documents;
    private final InsertManyOptions options;

    public InsertDocsRequestBody(String name, List<KeyValueDict> documents, InsertManyOptions options) {
        super(name);
        this.documents = documents;
        this.options = options;
    }
}
