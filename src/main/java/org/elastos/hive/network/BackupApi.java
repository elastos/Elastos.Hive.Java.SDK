package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BackupApi {

	@GET(Constance.API_PATH + "/backup/state")
	Call<ResponseBody> getState();

	@POST(Constance.API_PATH + "/backup/save_to_node")
	Call<ResponseBody> saveToNode(@Body RequestBody body);

	@POST(Constance.API_PATH + "/backup/restore_from_node")
	Call<ResponseBody> restoreFromNode(@Body RequestBody body);

	@POST(Constance.API_PATH + "/backup/activate_to_vault")
	Call<ResponseBody> activeToVault(@Body RequestBody body);

}
