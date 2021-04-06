package org.elastos.hive.database;

import com.google.gson.annotations.SerializedName;

public class InsertOneOptions {
    @SerializedName("bypass_document_validation")
    private final Boolean bypassDocumentValidation;

    public InsertOneOptions(boolean bypassDocumentValidation) {
        this.bypassDocumentValidation = bypassDocumentValidation;
    }
}
