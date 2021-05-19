package org.elastos.hive.vault.files;

import okhttp3.ResponseBody;
import org.elastos.hive.connection.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface FilesAPI {
	String API_UPLOAD = "/files/upload";

	@GET("/api/v1/files/list/folder")
	Call<FilesListResponseBody> listChildren(@Query("path") String filename);

	@POST("/api/v1/files/copy")
	Call<HiveResponseBody> copy(@Body FilesCopyRequestBody body);

	@POST("/api/v1/files/move")
	Call<HiveResponseBody> move(@Body FilesMoveRequestBody body);

	@POST("/api/v1/files/delete")
	Call<HiveResponseBody> delete(@Body FilesDeleteRequestBody body);

	@GET("/api/v1/files/properties")
	Call<FilesPropertiesResponseBody> properties(@Query("path") String filename);

	@GET("/api/v1/files/file/hash")
	Call<FilesHashResponseBody> hash(@Query("path") String filename);

	@GET("/api/v1/files/download")
	Call<ResponseBody> download(@Query("path") String filename);
}
