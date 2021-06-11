package org.elastos.hive.auth.controller;

import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;

import com.google.gson.annotations.SerializedName;

class ChallengeRequest {
	@SerializedName("challenge")
	private String challenge;

	boolean checkValid(String audience) {
		try {
			Claims claims = new JwtParserBuilder().build().parseClaimsJws(challenge).getBody();
			return claims.getExpiration().getTime() > System.currentTimeMillis()
					&& claims.getAudience().equals(audience);
		} catch (Exception e) {
			// TOOD: output log;
			return false;
		}
	}

	String getChallenge() {
		return challenge;
	}
}
