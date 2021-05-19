package org.elastos.hive.vault.auth;

import com.google.gson.annotations.SerializedName;

class AuthRequestBody {
    @SerializedName("jwt")
    private final String jwt;

    public AuthRequestBody(String jwt) {
        this.jwt = jwt;
    }
}
