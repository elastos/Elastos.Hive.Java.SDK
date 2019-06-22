package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.vendors.onedrive.Constance;
import org.elastos.hive.vendors.onedrive.Model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.Model.DriveResponse;
import org.elastos.hive.vendors.onedrive.Model.FileOrDirPropResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
}
