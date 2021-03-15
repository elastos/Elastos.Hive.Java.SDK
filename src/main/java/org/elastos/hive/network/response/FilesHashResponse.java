package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class FilesHashResponse extends ResponseBase {
    @SerializedName("SHA256")
    private String sha256;

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getSha256() {
        return this.sha256;
    }
}
