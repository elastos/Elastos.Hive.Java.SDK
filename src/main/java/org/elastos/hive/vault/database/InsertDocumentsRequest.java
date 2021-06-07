package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.KeyValueDict;

import java.util.List;

class InsertDocumentsRequest {
    @SerializedName("document")
    private List<KeyValueDict> documents;
    @SerializedName("options")
    private InsertDocumentsOptions options;

    public InsertDocumentsRequest setDocuments(List<KeyValueDict> documents) {
        this.documents = documents;
        return this;
    }

    public InsertDocumentsRequest setOptions(InsertDocumentsOptions options) {
        this.options = options;
        return this;
    }
}
