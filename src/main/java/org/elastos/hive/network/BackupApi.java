package org.elastos.hive.network;

import org.elastos.hive.network.request.BackupRestoreRequestBody;
import org.elastos.hive.network.request.BackupSaveRequestBody;
import org.elastos.hive.network.request.EmptyRequestBody;
import org.elastos.hive.network.response.BackupStateResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface BackupApi {

	/**
	 * Current backup process status on node side.
	 *
	 * @return backup response body
	 */
	@GET(BaseApi.API_VERSION + "/backup/state")
	Call<BackupStateResponseBody> getState();

	/**
	 * Save the database and files data to backup node server from vault node server.
	 *
	 * @param body request body
	 * @return response body
	 */
	@POST(BaseApi.API_VERSION + "/backup/save_to_node")
	Call<HiveResponseBody> saveToNode(@Body BackupSaveRequestBody body);

	/**
	 * Restore backup data to vault and replace the exist one.
	 *
	 * @param body request body
	 * @return response body
	 */
	@POST(BaseApi.API_VERSION + "/backup/restore_from_node")
	Call<HiveResponseBody> restoreFromNode(@Body BackupRestoreRequestBody body);

	/**
	 * Active backup data to vault on backup server side.
	 *
	 * @param body request body
	 * @return response body
	 */
	@POST(BaseApi.API_VERSION + "/backup/activate_to_vault")
	Call<HiveResponseBody> activeToVault(@Body EmptyRequestBody body);

}
