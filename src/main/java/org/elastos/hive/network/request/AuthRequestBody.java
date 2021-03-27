package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class AuthRequestBody {
    @SerializedName("jwt")
    private final String jwt;

    public AuthRequestBody(String jwt) {
        this.jwt = jwt;
    }
}
