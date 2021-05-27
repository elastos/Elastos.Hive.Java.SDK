package org.elastos.hive.auth.controller;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
	@POST("/api/v1/did/sign_in")
	Call<ChallengeRequest> signIn(@Body SigninRequest request);

	@POST("/api/v1/did/auth")
	Call<AccessToken> auth(@Body ChallengeResponse request);
}
