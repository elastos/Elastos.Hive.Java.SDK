package org.elastos.hive.vault.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
	@POST("/api/v1/did/sign_in")
	Call<SignInResponseBody> signIn(@Body SignInRequestBody reqBody);

	@POST("/api/v1/did/auth")
	Call<AuthResponseBody> auth(@Body AuthRequestBody reqBody);
}
