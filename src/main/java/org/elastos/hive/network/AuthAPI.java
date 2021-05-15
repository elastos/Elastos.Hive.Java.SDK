package org.elastos.hive.network;

import org.elastos.hive.network.request.AuthRequestBody;
import org.elastos.hive.network.request.SignInRequestBody;
import org.elastos.hive.network.response.AuthResponseBody;
import org.elastos.hive.network.response.SignInResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
	@POST("/api/v1/did/sign_in")
	Call<SignInResponseBody> signIn(@Body SignInRequestBody reqBody);

	@POST("/api/v1/did/auth")
	Call<AuthResponseBody> auth(@Body AuthRequestBody reqBody);
}
