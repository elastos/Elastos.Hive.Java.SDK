package org.elastos.hive.auth;

import com.google.gson.annotations.SerializedName;

class ChallengeResponse {
    @SerializedName("jwt")
    private final String jwt;

    public ChallengeResponse(String jwt) {
        this.jwt = jwt;
    }
}
