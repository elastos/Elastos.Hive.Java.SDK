package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
	@POST(Constance.API_PATH + "/did/sign_in")
	Call<ResponseBody> signIn(@Body RequestBody body);

	@POST(Constance.API_PATH + "/did/auth")
	Call<ResponseBody> auth(@Body RequestBody body);
}
