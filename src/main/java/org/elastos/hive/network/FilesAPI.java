package org.elastos.hive.network;

import okhttp3.ResponseBody;
import org.elastos.hive.network.request.FilesCopyRequestBody;
import org.elastos.hive.network.request.FilesDeleteRequestBody;
import org.elastos.hive.network.request.FilesMoveRequestBody;
import org.elastos.hive.network.response.FilesHashResponseBody;
import org.elastos.hive.network.response.FilesListResponseBody;
import org.elastos.hive.network.response.FilesPropertiesResponseBody;
import org.elastos.hive.network.response.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface FilesAPI {
	@GET("/api/v1/files/list/folder")
	Call<FilesListResponseBody> list(@Query("path") String filename);

	@GET("/api/v1/files/properties")
	Call<FilesPropertiesResponseBody> properties(@Query("path") String filename);

	@GET("/api/v1/files/download")
	Call<ResponseBody> download(@Query("path") String filename);

	@POST("/api/v1/files/delete")
	Call<HiveResponseBody> delete(@Body FilesDeleteRequestBody body);

	@POST("/api/v1/files/move")
	Call<HiveResponseBody> move(@Body FilesMoveRequestBody body);

	@POST("/api/v1/files/copy")
	Call<HiveResponseBody> copy(@Body FilesCopyRequestBody body);

	@GET("/api/v1/files/file/hash")
	Call<FilesHashResponseBody> hash(@Query("path") String filename);
}
