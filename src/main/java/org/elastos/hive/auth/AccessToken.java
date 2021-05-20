package org.elastos.hive.auth;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class AccessToken {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }
}
