package org.elastos.hive.network;

import org.elastos.hive.Constance;
import org.elastos.hive.files.FileInfo;
import org.elastos.hive.files.FilesList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FileApi {
	@GET(Constance.API_PATH + "/files/list/folder")
	Call<FilesList> files(@Query("path") String filename);

	@GET(Constance.API_PATH + "/files/download")
	Call<ResponseBody> downloader(@Query("path") String filename);

	@GET(Constance.API_PATH + "/files/properties")
	Call<FileInfo> getProperties(@Query("path") String filename);

	@POST(Constance.API_PATH + "/files/delete")
	Call<ResponseBody> deleteFolder(@Body RequestBody body);

	@POST(Constance.API_PATH + "/files/move")
	Call<ResponseBody> move(@Body RequestBody body);

	@POST(Constance.API_PATH + "/files/copy")
	Call<ResponseBody> copy(@Body RequestBody body);

	@GET(Constance.API_PATH + "/files/file/hash")
	Call<ResponseBody> hash(@Query("path") String filename);
}
