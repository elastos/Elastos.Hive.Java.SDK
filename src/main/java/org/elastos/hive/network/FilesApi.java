package org.elastos.hive.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.elastos.hive.network.response.FilesHashResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FilesApi {
	String API_UPLOAD = BaseApi.API_PATH + "/files/upload";

	@GET(BaseApi.API_PATH + "/files/download")
	Call<ResponseBody> download(@Query("path") String filename);

	@POST(BaseApi.API_PATH + "/files/delete")
	Call<ResponseBody> deleteFolder(@Body RequestBody body);

	@POST(BaseApi.API_PATH + "/files/move")
	Call<ResponseBody> move(@Body RequestBody body);

	@POST(BaseApi.API_PATH + "/files/copy")
	Call<ResponseBody> copy(@Body RequestBody body);

	@GET(BaseApi.API_PATH + "/files/file/hash")
	Call<FilesHashResponse> hash(@Query("path") String filename);
}
