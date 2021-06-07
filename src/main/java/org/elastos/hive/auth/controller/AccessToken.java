package org.elastos.hive.auth.controller;

import org.elastos.did.jwt.Claims;
import org.elastos.hive.utils.JwtUtil;

import com.google.gson.annotations.SerializedName;

class AccessToken {
	@SerializedName("token")
	private String token;

	boolean checkValid(String appInstanceDid) {
		Claims claims = JwtUtil.getBody(token);

		return claims.getExpiration().getTime() > System.currentTimeMillis()
				&& claims.getAudience().equals(appInstanceDid);
	}

	String getAccessToken() {
		return token;
	}
}
