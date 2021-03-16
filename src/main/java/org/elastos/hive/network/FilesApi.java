package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.elastos.hive.network.response.FilesHashResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FilesApi {
	String API_UPLOAD = "/files/upload";

	@GET(BaseApi.API_VERSION + "/files/download")
	Call<ResponseBody> download(@Query("path") String filename);

	@POST(BaseApi.API_VERSION + "/files/delete")
	Call<ResponseBody> deleteFolder(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/files/move")
	Call<ResponseBody> move(@Body RequestBody body);

	@POST(BaseApi.API_VERSION + "/files/copy")
	Call<ResponseBody> copy(@Body RequestBody body);

	@GET(BaseApi.API_VERSION + "/files/file/hash")
	Call<FilesHashResponseBody> hash(@Query("path") String filename);
}
