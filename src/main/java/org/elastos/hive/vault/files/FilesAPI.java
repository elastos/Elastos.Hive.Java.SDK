package org.elastos.hive.vault.files;

import okhttp3.ResponseBody;
import org.elastos.hive.connection.HiveResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

interface FilesAPI {
	String API_UPLOAD = "/api/v2/vault/files";

	@GET("/api/v2/vault/files/{path}")
	Call<ResponseBody> download(@Path("path") String path);

	@GET("/api/v2/vault/files/{path}?comp=children")
	Call<ListChildrenResponse> listChildren(@Path("path") String path);

	@GET("/api/v2/vault/files/{path}?comp=metadata")
	Call<FileInfo> getProperties(@Path("path") String path);

	@GET("/api/v2/vault/files/{path}?comp=hash")
	Call<FilesHashResponse> getHash(@Path("path") String path);

	@PUT("/api/v2/vault/files/{path}")
	Call<FilesCopyResponse> copy(@Path("path") String src,
								 @Query("dest") String dst);

	@PATCH("/api/v2/vault/files/{path}")
	Call<FilesMoveResponse> move(@Path("path") String src,
								 @Query("to") String dst);

	@DELETE("/api/v2/vault/files/{path}")
	Call<HiveResponseBody> delete(@Path("path") String path);
}
