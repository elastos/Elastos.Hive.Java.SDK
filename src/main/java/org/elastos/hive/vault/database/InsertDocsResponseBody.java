package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

class InsertDocsResponseBody extends HiveResponseBody {
    private Boolean acknowledged;
    @SerializedName("inserted_ids")
    private List<String> insertedIds;

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public List<String> getInsertedIds() {
        return insertedIds;
    }
}
