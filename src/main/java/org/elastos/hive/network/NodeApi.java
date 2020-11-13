package org.elastos.hive.network;

import org.elastos.hive.Constance;
import org.elastos.hive.files.FileInfo;
import org.elastos.hive.files.FilesList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NodeApi {

	@GET(Constance.API_PATH + "/hive/version")
	Call<ResponseBody> getVersion();

	@GET(Constance.API_PATH + "/hive/commithash")
	Call<ResponseBody> getCommitId();

	@POST(Constance.API_PATH + "/did/sign_in")
	Call<ResponseBody> signIn(@Body RequestBody body);

	@POST(Constance.API_PATH + "/did/auth")
	Call<ResponseBody> auth(@Body RequestBody body);

	@POST(Constance.API_PATH + "/sync/setup/google_drive")
	Call<ResponseBody> googleDrive(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/create_collection")
	Call<ResponseBody> createCollection(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_collection")
	Call<ResponseBody> deleteCollection(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/insert_one")
	Call<ResponseBody> insertOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/insert_many")
	Call<ResponseBody> insertMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/update_one")
	Call<ResponseBody> updateOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/update_many")
	Call<ResponseBody> updateMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_one")
	Call<ResponseBody> deleteOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_many")
	Call<ResponseBody> deleteMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/count_documents")
	Call<ResponseBody> countDocs(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/find_one")
	Call<ResponseBody> findOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/find_many")
	Call<ResponseBody> findMany(@Body RequestBody body);

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

	@POST(Constance.API_PATH + "/scripting/set_subcondition")
	Call<ResponseBody> registerCondition(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/set_script")
	Call<ResponseBody> registerScript(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Body RequestBody body);

	@Multipart
	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Part MultipartBody.Part file, @Part("metadata") RequestBody metadata);
}
