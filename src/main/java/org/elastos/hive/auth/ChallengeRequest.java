package org.elastos.hive.auth;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.utils.JwtUtil;

import com.google.gson.annotations.SerializedName;

class ChallengeRequest {
    @SerializedName("challenge")
    private String challenge;

    public String getChallenge() {
        return challenge;
    }

    public static Claims checkValid(String jwt, String validAudience) {
        Claims claims = JwtUtil.getBody(jwt);

        if (claims.getExpiration().getTime() <= System.currentTimeMillis() )
            throw new HiveSdkException("Bad jwt expiration date");

        if (!claims.getAudience().equals(validAudience))
            throw new HiveSdkException("Bad jwt audience value");

        return claims;
    }

    public Claims checkValid(String validAudience) {
        return checkValid(challenge, validAudience);
    }
}
