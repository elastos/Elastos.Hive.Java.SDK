package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class AccessToken {
	@SerializedName("token")
	private String token;

	String getAccessToken() {
		return token;
	}
}
