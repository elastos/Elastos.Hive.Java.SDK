package org.elastos.hive.network;

import org.elastos.hive.network.request.AuthAuthRequestBody;
import org.elastos.hive.network.request.AuthSignInRequestBody;
import org.elastos.hive.network.response.AuthAuthResponseBody;
import org.elastos.hive.network.response.AuthSignInResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
	@POST(BaseApi.API_VERSION + "/did/sign_in")
	Call<AuthSignInResponseBody> signIn(@Body AuthSignInRequestBody reqBody);

	@POST(BaseApi.API_VERSION + "/did/auth")
	Call<AuthAuthResponseBody> auth(@Body AuthAuthRequestBody reqBody);
}
