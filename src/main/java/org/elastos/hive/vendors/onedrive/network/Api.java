package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.vendors.onedrive.Constance;
import org.elastos.hive.vendors.onedrive.Model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.Model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.Model.DirInfoResponse;
import org.elastos.hive.vendors.onedrive.Model.DirMoveAndCopyReqest;
import org.elastos.hive.vendors.onedrive.Model.DriveResponse;
import org.elastos.hive.vendors.onedrive.Model.FileOrDirPropResponse;
import org.elastos.hive.vendors.onedrive.Model.NoBodyEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Package: org.elastos.hive.vendors.onedrive.network
 * ClassName: Api
 * Created by ranwang on 2019/6/17.
 */
public interface Api {

    //if @call https://graph.microsoft.com/v1.0/me/
//    @GET(".")
//    Call<ClientResponse> getInfo();

    //current @call https://graph.microsoft.com/v1.0/me/drive
    @GET(Constance.DRIVE)
    Call<DriveResponse> getInfo();

    @GET(Constance.DRIVE)
    Call<DriveResponse> getDrive();

    @GET(Constance.DRIVE+"/{path}")
    Call<FileOrDirPropResponse> getFileOrDirProp(@Path("path") String pathName);

    @PUT(Constance.DRIVE+"/root:{path}:/content")
    Call<FileOrDirPropResponse> createFile(@Path("path") String path);

    @POST(Constance.DRIVE+"/{path}")
    Call<FileOrDirPropResponse> createDir(@Path("path") String path , @Body CreateDirRequest dirRequest);

    @GET(Constance.DRIVE+"/root:/{path}")
    Call<DirInfoResponse> getDirInfo(@Path("path")String path);

    @PATCH(Constance.DRIVE+"/root:{path}")
    Call<NoBodyEntity> moveTo(@Path("path")String path , @Body DirMoveAndCopyReqest dirMoveAndCopyReqest);

    @POST(Constance.DRIVE+"/root:{path}:/copy")
    Call<NoBodyEntity> copyTo(@Path("path")String path , @Body DirMoveAndCopyReqest dirMoveAndCopyReqest);

    @DELETE(Constance.DRIVE+"/root:{path}")
    Call<NoBodyEntity> deleteItem(@Path("path")String path);

    @POST(Constance.DRIVE+"/root:{path}:/children")
    Call<FileOrDirPropResponse> createDirFromDir(@Path("path") String path , @Body CreateDirRequest dirRequest);

    @GET(Constance.DRIVE+"/root:{path}")
    Call<FileOrDirPropResponse> getDirFromDir(@Path("path") String path);

    @GET(Constance.DRIVE+"/root:{path}")
    Call<FileOrDirPropResponse> getFileFromDir(@Path("path") String path);

    @GET(Constance.DRIVE+"/root:{path}:/children")
    Call<DirChildrenResponse> getChildren(@Path("path") String path);
}
