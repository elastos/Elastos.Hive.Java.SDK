package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class InsertDocResponseBody extends HiveResponseBody {
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
