package org.elastos.hive.vendor.vault.network;



import org.elastos.hive.vendor.vault.network.model.BaseResponse;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;

import java.util.Map;


import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VaultApi {

    //{"did":"iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk", "password":"adujejd"}
    @POST(ConnectConstance.API_PATH + "/did/register")
    Call<BaseResponse> register(@FieldMap Map<String, Object> map);

    //{"did":"iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk", "password":"adujejd"}
    @POST(ConnectConstance.API_PATH + "/did/login")
    Call<TokenResponse> login(@Body RequestBody body);

    //{ "collection":"works","schema": {"title": {"type": "string"}, "author": {"type": "string"}}}
    @POST(ConnectConstance.API_PATH + "/db/create_collection")
    Call<BaseResponse> createCollection(@FieldMap Map<String, Object> map);

    //
    @POST(ConnectConstance.API_PATH + "db/col/{path}")
    Call<BaseResponse> dbCol(@Path("path") String path);

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
