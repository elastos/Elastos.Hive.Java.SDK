package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class FilesHashResponseBody extends HiveResponseBody {
    @SerializedName("SHA256")
    private String sha256;

    public String getSha256() {
        return this.sha256;
    }
}
