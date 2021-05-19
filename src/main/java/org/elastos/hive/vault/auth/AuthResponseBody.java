package org.elastos.hive.vault.auth;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class AuthResponseBody extends HiveResponseBody {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }
}
