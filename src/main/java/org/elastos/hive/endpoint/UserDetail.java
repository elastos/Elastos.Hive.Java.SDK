package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

public class UserDetail {
    @SerializedName("did")
    private String did;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
