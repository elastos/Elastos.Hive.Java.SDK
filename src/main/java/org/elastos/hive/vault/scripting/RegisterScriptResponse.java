package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class RegisterScriptResponse {
    @SerializedName("acknowledged")
    private Boolean acknowledged;
    @SerializedName("matched_count")
    private Integer matchedCount;
    @SerializedName("modified_count")
    private Integer modifiedCount;
    @SerializedName("upserted_id")
    private String upsertedId;
}
