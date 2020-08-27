package org.elastos.hive.vendor.vault.network;


import org.elastos.hive.vendor.vault.network.model.AuthResponse;
import org.elastos.hive.vendor.vault.network.model.BaseResponse;
import org.elastos.hive.vendor.vault.network.model.FilesResponse;
import org.elastos.hive.vendor.vault.network.model.PropertiesResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;
import org.elastos.hive.vendor.vault.network.model.UploadResponse;

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
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VaultApi {

    @POST(ConnectConstance.API_PATH + "/did/auth")
    Call<AuthResponse> auth(@Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/sync/setup/google_drive")
    Call<BaseResponse> googleDrive(@Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/db/create_collection")
    Call<BaseResponse> createCollection(@Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/db/col/{path}")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBody> post_dbCol(@Path("path") String path, @Body RequestBody body);

    @GET(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> get_dbCol(@Path("path") String path, @Query("where") String json);

    @PUT(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> put_dbCol(@Path("path") String path, @Header("If-Match") String match, @Body RequestBody body);

    @PATCH(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> patch_dbCol(@Path("path") String path, @Header("If-Match") String match, @Body RequestBody body);

    @DELETE(ConnectConstance.API_PATH + "/db/col/{path}")
    Call<ResponseBody> delete_dbCol(@Path("path") String path, @Header("If-Match") String match);

    //file="path/of/file/name"
//    @Multipart
//    @POST(ConnectConstance.API_PATH + "/file/uploader")
//    Call<BaseResponse> uploader(@Part MultipartBody.Part part);

    @GET(ConnectConstance.API_PATH + "/files/list/folder")
    Call<FilesResponse> files(@Query("name") String filename);

    @POST(ConnectConstance.API_PATH + "/files/creator/file")
    Call<UploadResponse> createFile(@Body RequestBody body);

    @POST("{path}")
    Call<BaseResponse> uploadFile(@Path("path") String path, @Body RequestBody body);

    @GET(ConnectConstance.API_PATH + "/files/downloader")
    Call<ResponseBody> downloader(@Query("name") String filename);

    @POST(ConnectConstance.API_PATH + "/files/deleter/file")
    Call<BaseResponse> deleteFile(@Body RequestBody body);

    @GET(ConnectConstance.API_PATH + "/files/properties")
    Call<PropertiesResponse> getProperties(@Query("name") String filename);

    //TODO
    // {name="path/of/folder/name"}
    @POST(ConnectConstance.API_PATH + "/files/creator/folder")
    Call<BaseResponse> createFolder(@Body RequestBody body);

    //{"name": "test.png"}
    @POST(ConnectConstance.API_PATH + "/files/deleter/folder")
    Call<BaseResponse> deleteFolder(@Body RequestBody body);

    //{"src_name": "path/of/src/folder/or/file",
    //            "dst_name": "path/of/dst/folder/or/file",
    //        }
    @POST(ConnectConstance.API_PATH + "/files/mover")
    Call<BaseResponse> move(@Body RequestBody body);

    //{"src_name": "path/of/src/folder/or/file",
    //            "dst_name": "path/of/dst/folder/or/file",
    //        }
    @POST(ConnectConstance.API_PATH + "/files/copier")
    Call<BaseResponse> copy(@Body RequestBody body);

    @GET(ConnectConstance.API_PATH + "/files/file/hash")
    Call<BaseResponse> hash(@Query("name") String filename);

    @POST(ConnectConstance.API_PATH + "/scripting/set_subcondition")
    Call<BaseResponse> registerCondition(@Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/scripting/set_script")
    Call<BaseResponse> registerScript(@Body RequestBody body);

    @POST(ConnectConstance.API_PATH + "/scripting/run_script")
    Call<BaseResponse> callScript(@Body RequestBody body);


}
