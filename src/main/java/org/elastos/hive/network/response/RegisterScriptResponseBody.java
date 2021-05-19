package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

public class RegisterScriptResponseBody extends HiveResponseBody {
    @SerializedName("acknowledged")
    private Boolean acknowledged;
    @SerializedName("matched_count")
    private Integer matchedCount;
    @SerializedName("modified_count")
    private Integer modifiedCount;
    @SerializedName("upserted_id")
    private String upsertedId;
}
