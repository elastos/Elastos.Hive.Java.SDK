package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class AuthAuthResponse extends ResponseBase {
    @SerializedName("access_token")
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
