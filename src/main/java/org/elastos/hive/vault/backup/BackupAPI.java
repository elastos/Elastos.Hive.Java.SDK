package org.elastos.hive.vault.backup;

import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.connection.EmptyRequestBody;
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
