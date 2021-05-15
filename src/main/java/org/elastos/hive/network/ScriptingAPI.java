package org.elastos.hive.network;

import okhttp3.ResponseBody;
import org.elastos.hive.network.request.CallScriptRequestBody;
import org.elastos.hive.network.request.RegisterScriptRequestBody;
import org.elastos.hive.network.response.RegisterScriptResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ScriptingAPI {
	String API_SCRIPT_UPLOAD = "/scripting/run_script_upload";

	@POST("/api/v1/scripting/set_script")
	Call<RegisterScriptResponseBody> registerScript(@Body RegisterScriptRequestBody body);

	@POST("/api/v1/scripting/run_script")
	Call<ResponseBody> callScript(@Body CallScriptRequestBody body);

	@GET("/api/v1/scripting/run_script_url/{targetDid}@{appDid}/{scriptName}")
	Call<ResponseBody> callScriptUrl(@Path("targetDid") String targetDid,
									 @Path("appDid") String appDid,
									 @Path("scriptName") String scriptName,
									 @Query("params") String params);

	@POST("/api/v1/scripting/run_script_download/{transaction_id}")
	Call<ResponseBody> callDownload(@Path("transaction_id") String transactionId);
}
