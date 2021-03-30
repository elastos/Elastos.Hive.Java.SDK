package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class DeleteDocResponseBody extends HiveResponseBody {
    private String acknowledged;
    @SerializedName("deleted_count")
    private Integer deletedCount;

    public String getAcknowledged() {
        return acknowledged;
    }

    public Integer getDeletedCount() {
        return deletedCount;
    }
}
