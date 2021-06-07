package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class UpdateResult {
    private Boolean acknowledged;
    @SerializedName("matched_count")
    private int matchedCount;
    @SerializedName("modified_count")
    private int modifiedCount;
    @SerializedName("upserted_id")
    private String upsertedId;

    public Boolean getAcknowledged() {
        return acknowledged;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public int getModifiedCount() {
        return modifiedCount;
    }

    public String getUpsertedId() {
        return upsertedId;
    }
}
