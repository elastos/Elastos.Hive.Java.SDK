package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.elastos.hive.network.request.RegisterScriptRequestBody;
import org.elastos.hive.network.response.RegisterScriptResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ScriptingApi {
	String API_SCRIPT_UPLOAD = "/scripting/run_script_upload";

	@POST(BaseApi.API_VERSION + "/scripting/set_script")
	Call<RegisterScriptResponseBody> registerScript(@Body RegisterScriptRequestBody body);

	@POST(BaseApi.API_VERSION + "/scripting/run_script")
	Call<ResponseBody> callScript(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/scripting/run_script_download/{transaction_id}")
	Call<ResponseBody> callDownload(@Path("transaction_id") String transactionId);

	@GET(BaseApi.API_VERSION+"/scripting/run_script_url/{targetDid}@{appDid}/{scriptName}")
	Call<ResponseBody> callScriptUrl(@Path("targetDid") String targetDid,
									 @Path("appDid") String appDid,
									 @Path("scriptName") String scriptName,
									 @Query("params") String params);
}
