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
    @GET(ConnectConstance.API_PATH + "/did/auth")
    Call<AuthResponse> auth(@Body RequestBody body);

    //{"subject":"didauth",
    //           "iss":"did:elastos:iWFAUYhTa35c1fPe3iCJvihZHx6quumnym",
    //           "realm": "elastos_hive_node",
    //           "nonce" : "4607e6de-b5f0-11ea-a859-f45c898fba57"
    //           "key_name" : "key2",
    //           "sig" : "iWFAUYhTa35c1fPiWFAUYhTa35c1fPe3iCJvihZHx6quumnyme3iCJvihZHx6quumnymiWFAUYhTa35c1fPe3iCJvihZHx6quumnym"
    //           }
    @GET(ConnectConstance.API_PATH + "/did/{path}/callback")
    Call<TokenResponse> authCallback(@Path("path") String path, @Body RequestBody body);

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

    @GET(ConnectConstance.API_PATH + "/file/list")
    Call<FilesResponse> files();

    @GET(ConnectConstance.API_PATH + "/file/downloader")
    Call<ResponseBody> downloader(@Query("filename") String filename);

    //{"file_name": "test.png"}
    @POST(ConnectConstance.API_PATH + "/file/delete")
    Call<BaseResponse> delete(@Body RequestBody body);
}
