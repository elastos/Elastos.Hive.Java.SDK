package org.elastos.hive.vault.backup;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

interface BackupAPI {
	@GET("/api/v1/backup/state")
	Call<BackupResult> getState();

	@POST("/api/v1/backup/save_to_node")
	Call<Void> saveToNode(@Body RequestParams params);

	@POST("/api/v1/backup/restore_from_node")
	Call<Void> restoreFromNode(@Body RequestParams params);

	@POST("/api/v1/backup/activate_to_vault")
	Call<Void> activeToVault();
}
