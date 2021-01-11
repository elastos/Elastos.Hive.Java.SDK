package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackupApi {

	@POST(Constance.API_PATH + "/backup/state")
	Call<ResponseBody> getState();

	@POST(Constance.API_PATH + "/backup/save/to/node")
	Call<ResponseBody> saveToNode(@Body RequestBody body);

	@POST(Constance.API_PATH + "/backup/restore/from/node")
	Call<ResponseBody> restoreFromNode(@Body RequestBody body);

	@POST(Constance.API_PATH + "/backup/active/to/vault")
	Call<ResponseBody> activeToVault(@Body RequestBody body);

}
