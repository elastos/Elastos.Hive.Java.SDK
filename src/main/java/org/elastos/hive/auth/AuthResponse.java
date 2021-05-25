package org.elastos.hive.auth;

import com.google.gson.annotations.SerializedName;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.connection.HiveResponseBody;

class AuthResponse extends HiveResponseBody {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }

    public Claims checkValid(String validAudience) {
        return SignInResponse.checkValid(token, validAudience);
    }
}
