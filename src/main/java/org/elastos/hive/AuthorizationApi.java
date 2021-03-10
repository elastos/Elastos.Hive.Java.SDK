package org.elastos.hive;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthorizationApi extends BaseApi{

	@POST(API_VERSION + "/did/sign_in")
	Call<ResponseBody> signIn(@Body RequestBody body);

	@POST(API_VERSION + "/did/auth")
	Call<ResponseBody> auth(@Body RequestBody body);


}
