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

public interface FilesApi {
	String API_UPLOAD = "/files/upload";

	@GET(BaseApi.API_VERSION + "/files/list/folder")
	Call<FilesListResponseBody> list(@Query("path") String filename);

	@GET(BaseApi.API_VERSION + "/files/properties")
	Call<FilesPropertiesResponseBody> properties(@Query("path") String filename);

	@GET(BaseApi.API_VERSION + "/files/download")
	Call<ResponseBody> download(@Query("path") String filename);

	@POST(BaseApi.API_VERSION + "/files/delete")
	Call<HiveResponseBody> delete(@Body FilesDeleteRequestBody body);

	@POST(BaseApi.API_VERSION + "/files/move")
	Call<HiveResponseBody> move(@Body FilesMoveRequestBody body);

	@POST(BaseApi.API_VERSION + "/files/copy")
	Call<HiveResponseBody> copy(@Body FilesCopyRequestBody body);

	@GET(BaseApi.API_VERSION + "/files/file/hash")
	Call<FilesHashResponseBody> hash(@Query("path") String filename);
}
