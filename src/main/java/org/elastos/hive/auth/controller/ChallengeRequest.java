package org.elastos.hive.auth.controller;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.utils.JwtUtil;

import com.google.gson.annotations.SerializedName;

class ChallengeRequest {
    @SerializedName("challenge")
    private String challenge;

    static String getValidJwt(String jwt, String validAudience) {
        Claims claims = JwtUtil.getBody(jwt);

        if (claims.getExpiration().getTime() <= System.currentTimeMillis() )
            throw new HiveSdkException("Bad jwt expiration date");

        if (!claims.getAudience().equals(validAudience))
            throw new HiveSdkException("Bad jwt audience value");

        return jwt;
    }

    String getValidChallenge(String appInstanceDid) {
        return getValidJwt(challenge, appInstanceDid);
    }
}
