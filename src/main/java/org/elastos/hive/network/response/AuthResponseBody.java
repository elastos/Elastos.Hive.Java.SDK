package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponseBody extends ResponseBodyBase {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }
}
