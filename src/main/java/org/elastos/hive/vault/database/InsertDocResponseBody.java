package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class InsertDocResponseBody extends HiveResponseBody {
    private Boolean acknowledged;
    @SerializedName("inserted_id")
    private String insertedId;

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public String getInsertedId() {
        return insertedId;
    }
}
