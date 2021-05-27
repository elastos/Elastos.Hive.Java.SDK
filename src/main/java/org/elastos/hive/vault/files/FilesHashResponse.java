package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class FilesHashResponse {
    @SerializedName("name")
    private String name;
    @SerializedName("algorithm")
    private String algorithm;
    @SerializedName("hash")
    private String hash;

    public String getName() {
        return name;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getHash() {
        return hash;
    }
}
