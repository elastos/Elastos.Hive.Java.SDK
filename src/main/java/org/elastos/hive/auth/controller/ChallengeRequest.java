package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class ChallengeRequest {
	@SerializedName("challenge")
	private String challenge;

	String getChallenge() {
		return challenge;
	}
}
