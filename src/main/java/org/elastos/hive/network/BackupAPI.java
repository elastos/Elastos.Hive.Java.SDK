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

interface BackupAPI {
	@GET("/api/v1/backup/state")
	Call<BackupStateResponseBody> getState();

	@POST("/api/v1/backup/save_to_node")
	Call<HiveResponseBody> saveToNode(@Body BackupSaveRequestBody body);

	@POST("/api/v1/backup/restore_from_node")
	Call<HiveResponseBody> restoreFromNode(@Body BackupRestoreRequestBody body);

	@POST("/api/v1/backup/activate_to_vault")
	Call<HiveResponseBody> activeToVault(@Body EmptyRequestBody body);
}
