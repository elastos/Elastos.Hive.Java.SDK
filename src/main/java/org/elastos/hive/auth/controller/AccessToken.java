package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class AccessToken {
	@SerializedName("access_token")
	private String token;

	String getValidAccessToken(String appInstanceDid) {
		return ChallengeRequest.getValidJwt(token, appInstanceDid);
	}
}
