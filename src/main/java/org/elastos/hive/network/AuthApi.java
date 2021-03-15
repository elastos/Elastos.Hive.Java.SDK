package org.elastos.hive.network;

import org.elastos.hive.network.response.AuthAuthResponse;
import org.elastos.hive.network.response.AuthSignInResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface AuthApi {
	@POST(BaseApi.API_VERSION + "/did/sign_in")
	Call<AuthSignInResponse> signIn(@Body Map<String, Object> params);

	@POST(BaseApi.API_VERSION + "/did/auth")
	Call<AuthAuthResponse> auth(@Body Map<String, Object> params);
}
