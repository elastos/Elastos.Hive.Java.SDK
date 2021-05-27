package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;
import org.elastos.did.jwt.Claims;

class AccessToken {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }

    public Claims checkValid(String validAudience) {
        return ChallengeRequest.checkValid(token, validAudience);
    }
}
