package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

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
