package org.elastos.hive.network;

import org.elastos.hive.Constance;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ScriptingApi {

	@POST(Constance.API_PATH + "/scripting/set_subcondition")
	Call<ResponseBody> registerCondition(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/set_script")
	Call<ResponseBody> registerScript(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Body RequestBody body);

	@Multipart
	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Part MultipartBody.Part file, @Part("metadata") RequestBody metadata);
}
