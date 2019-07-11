package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.vendors.onedrive.OneDriveConstance;
import org.elastos.hive.vendors.onedrive.network.Model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.network.Model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.network.Model.MoveAndCopyReqest;
import org.elastos.hive.vendors.onedrive.network.Model.DriveResponse;
import org.elastos.hive.vendors.onedrive.network.Model.FileOrDirPropResponse;
import org.elastos.hive.vendors.connection.Model.NoBodyEntity;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OneDriveApi {

    //if @call https://graph.microsoft.com/v1.0/me/
//    @GET(".")
//    Call<ClientResponse> getInfo();

    //current @call https://graph.microsoft.com/v1.0/me/drive
    @GET(OneDriveConstance.DRIVE)
    Call<DriveResponse> getInfo();

    @GET(OneDriveConstance.DRIVE)
    Call<DriveResponse> getDrive();

    @GET(OneDriveConstance.DRIVE+"/{path}")
    Call<FileOrDirPropResponse> getFileOrDirProp(@Path("path") String pathName);

    @PUT(OneDriveConstance.DRIVE+"/root:{path}:/content")
    Call<FileOrDirPropResponse> createFile(@Path("path") String path);

    @POST(OneDriveConstance.DRIVE+"/{path}")
    Call<FileOrDirPropResponse> createDir(@Path("path") String path , @Body CreateDirRequest dirRequest);

    @GET(OneDriveConstance.DRIVE+"/root:/{path}")
    Call<FileOrDirPropResponse> getDirAndFileInfo(@Path("path")String path);

    @PATCH(OneDriveConstance.DRIVE+"/root:{path}")
    Call<NoBodyEntity> moveTo(@Path("path")String path , @Body MoveAndCopyReqest moveAndCopyReqest);

    @POST(OneDriveConstance.DRIVE+"/root:{path}:/copy")
    Call<NoBodyEntity> copyTo(@Path("path")String path , @Body MoveAndCopyReqest moveAndCopyReqest);

    @DELETE(OneDriveConstance.DRIVE+"/root:{path}")
    Call<NoBodyEntity> deleteItem(@Path("path")String path);

    @POST(OneDriveConstance.DRIVE+"/root:{path}:/children")
    Call<FileOrDirPropResponse> createDirFromDir(@Path("path") String path , @Body CreateDirRequest dirRequest);

    @GET(OneDriveConstance.DRIVE+"/root:{path}")
    Call<FileOrDirPropResponse> getDirFromDir(@Path("path") String path);

    @GET(OneDriveConstance.DRIVE+"/root:{path}")
    Call<FileOrDirPropResponse> getFileFromDir(@Path("path") String path);

    @GET(OneDriveConstance.DRIVE+"/root:{path}:/children")
    Call<DirChildrenResponse> getChildren(@Path("path") String path);

    @GET(OneDriveConstance.DRIVE+"/root:{path}:/content")
    Call<ResponseBody> read(@Path("path") String path);

    @PUT(OneDriveConstance.DRIVE+"/root:{path}:/content")
    Call<NoBodyEntity> write(@Path("path") String path, @Body RequestBody body);
}
