package org.elastos.hive.auth;

import com.google.gson.annotations.SerializedName;

class AuthRequest {
    @SerializedName("jwt")
    private final String jwt;

    public AuthRequest(String jwt) {
        this.jwt = jwt;
    }
}
