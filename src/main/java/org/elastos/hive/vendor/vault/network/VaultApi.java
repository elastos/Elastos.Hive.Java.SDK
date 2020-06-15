package org.elastos.hive.vendor.vault.network;



import org.elastos.hive.vendor.vault.network.model.BaseResponse;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VaultApi {

    //{"did":"iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk", "password":"adujejd"}
    @POST(ConnectConstance.API_PATH + "/did/register")
    Call<BaseResponse> register(@FieldMap Map<String, Object> map);

    //{"did":"iUWjzkS4Di75yCXiKJqxrHYxQdBcS2NaPk", "password":"adujejd"}
    @POST(ConnectConstance.API_PATH + "/did/login")
    Call<TokenResponse> login(@FieldMap Map<String, Object> map);

    //{ "collection":"works","schema": {"title": {"type": "string"}, "author": {"type": "string"}}}
    @POST(ConnectConstance.API_PATH + "/db/create_collection")
    Call<BaseResponse> createCollection(@FieldMap Map<String, Object> map);

    //
    @POST(ConnectConstance.API_PATH + "db/col/{path}")
    Call<BaseResponse> dbCol(@Path("path") String path);

    //file="path/of/file/name"
    @POST(ConnectConstance.API_PATH + "/file/uploader")
    Call<BaseResponse> uploader(@Path("path") String path, @Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/file/list")
    Call<FilesResponse> files();

    @GET(ConnectConstance.API_PATH + "/file/downloader/{filename}")
    Call<ResponseBody> downloader(@Path(("filename")) String filename);

    //{"file_name": "test.png"}
    @POST(ConnectConstance.API_PATH + "/file/delete")
    Call<BaseResponse> delete(@FieldMap Map<String, Object> map);
}
