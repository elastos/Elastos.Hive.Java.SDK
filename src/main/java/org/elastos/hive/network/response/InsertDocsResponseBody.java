package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsertDocsResponseBody extends HiveResponseBody {
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
