package org.elastos.hive.network;

import okhttp3.ResponseBody;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface CallAPI {

	// scripting
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

	// vault

	@POST("/api/v1/service/vault/create")
	Call<VaultCreateResponseBody> createVault();

	@POST("/api/v1/service/vault/freeze")
	Call<HiveResponseBody> freeze();

	@POST("/api/v1/service/vault/unfreeze")
	Call<HiveResponseBody> unfreeze();

	@POST("/api/v1/service/vault/remove")
	Call<HiveResponseBody> removeVault();

	@GET("/api/v1/service/vault")
	Call<VaultInfoResponseBody> getVaultInfo();

	@POST("/api/v1/service/vault_backup/create")
	Call<VaultCreateResponseBody> createBackupVault();

	@GET("/api/v1/service/vault_backup")
	Call<VaultInfoResponseBody> getBackupVaultInfo();

}
