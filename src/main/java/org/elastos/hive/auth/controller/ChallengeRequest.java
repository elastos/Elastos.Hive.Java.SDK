package org.elastos.hive.auth.controller;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.utils.JwtUtil;

import com.google.gson.annotations.SerializedName;

class ChallengeRequest {
	@SerializedName("challenge")
	private String challenge;

	boolean checkValid(String validAudience) {
		Claims claims = JwtUtil.getBody(challenge);

		return claims.getExpiration().getTime() > System.currentTimeMillis()
				&& claims.getAudience().equals(validAudience);
	}

	String getChallenge() {
		return challenge;
	}
}
