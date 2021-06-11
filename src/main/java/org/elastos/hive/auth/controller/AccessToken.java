package org.elastos.hive.auth.controller;

import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;

import com.google.gson.annotations.SerializedName;

class AccessToken {
	@SerializedName("token")
	private String token;

	boolean checkValid(String appInstanceDid) {
		try {
			Claims claims = new JwtParserBuilder().build().parseClaimsJws(token).getBody();
			return claims.getExpiration().getTime() > System.currentTimeMillis()
					&& claims.getAudience().equals(appInstanceDid);
		} catch (Exception e) {
			// TOOD: output log;
			return false;
		}
	}

	String getAccessToken() {
		return token;
	}
}
