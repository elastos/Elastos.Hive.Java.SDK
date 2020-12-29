package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ScriptingApi {

	@POST(Constance.API_PATH + "/scripting/set_script")
	Call<ResponseBody> registerScript(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/run_script_download/{transaction_id}")
	Call<ResponseBody> callDownload(@Path("transaction_id") String transactionId);
}
