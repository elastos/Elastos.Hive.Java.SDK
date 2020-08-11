package org.elastos.hive.vendor.vault.network;


import org.elastos.hive.vendor.vault.network.model.AuthResponse;
import org.elastos.hive.vendor.vault.network.model.BaseResponse;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VaultApi {

    //{"iss":" "did:elastos:iWFAUYhTa35c1fPe3iCJvihZHx6quumnym"}
    @POST(ConnectConstance.API_PATH + "/did/auth")
    Call<AuthResponse> auth(@Body RequestBody body);

    //   data: {
    //           "token": "ya29.a0AfH6SMAVaP_gNAdbF25L5hktoPRdV8mBkcra6UaneG2w7ZYSusXevycqvhUrGrQ_FpsBPYYvxq2Sdx13zEwG1-m8I-pSFV05UY52X6wNnVlpxG7hsyBteEdUiiQPDT52zbK5ceQZ4-cpfXSlrplsQ8kZvPYC5nR1yks", "refresh_token": "1//06llFKBe-DBkRCgYIARAAGAYSNwF-L9Irfka2E6GP-J9gKBZN5AQS3z19vHOtjHq67p2ezCsJiVUZO-jKMSDKLgkiGfXgmBYimwc", "token_uri": "https://oauth2.googleapis.com/token", "client_id": "24235223939-7335upec07n0c3qc7mnd19jqoeglrg3t.apps.googleusercontent.com", "client_secret": "-7Ls5u1NpRe77Dy6VkL5W4pe", "scopes": ["https://www.googleapis.com/auth/drive.file"], "expiry": "2020-06-24 03:10:49.960710"
    //            }
    @POST(ConnectConstance.API_PATH + "/sync/setup/google_drive")
    Call<BaseResponse> googleDrive(@Body RequestBody body);

    //{ "collection":"works","schema": {"title": {"type": "string"}, "author": {"type": "string"}}}
    @POST(ConnectConstance.API_PATH + "/db/create_collection")
    Call<BaseResponse> createCollection(@FieldMap Map<String, Object> map);

    @POST(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<BaseResponse> post_dbCol(@Path("path") String path, @Body RequestBody body);

    @GET(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> get_dbCol(@Path("path") String path, @Query("where") String json);

    @PUT(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> put_dbCol(@Path("path") String path, @Header("If-Match") String match, @Body RequestBody body);

    @PATCH(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> patch_dbCol(@Path("path") String path, @Header("If-Match") String match, @Body RequestBody body);

    @DELETE(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> delete_dbCol(@Path("path") String path, @Header("If-Match") String match);

    //file="path/of/file/name"
    @Multipart
    @POST(ConnectConstance.API_PATH + "/file/uploader")
    Call<BaseResponse> uploader(@Part MultipartBody.Part part);

    @GET(ConnectConstance.API_PATH + "/files/list/folder")
    Call<FilesResponse> files();

    @GET(ConnectConstance.API_PATH + "/file/downloader")
    Call<ResponseBody> downloader(@Query("filename") String filename);

    //{"file_name": "test.png"}
    @POST(ConnectConstance.API_PATH + "/file/delete")
    Call<BaseResponse> delete(@Body RequestBody body);


}
