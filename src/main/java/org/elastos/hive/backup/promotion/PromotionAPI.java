package org.elastos.hive.backup.promotion;

import org.elastos.hive.connection.EmptyRequestBody;
import org.elastos.hive.connection.HiveResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface PromotionAPI {
	@POST("/api/v1/backup/activate_to_vault")
	Call<HiveResponseBody> activeToVault(@Body EmptyRequestBody body);
}

