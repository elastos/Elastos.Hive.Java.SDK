package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class AuthSignInResponse extends ResponseBase {
    @SerializedName("challenge")
    private String challenge;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}
