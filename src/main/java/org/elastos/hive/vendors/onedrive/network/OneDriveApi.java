/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.onedrive.network;

import org.elastos.hive.vendors.onedrive.OneDriveConstance;
import org.elastos.hive.vendors.onedrive.network.model.CreateDirRequest;
import org.elastos.hive.vendors.onedrive.network.model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.network.model.MoveAndCopyReqest;
import org.elastos.hive.vendors.onedrive.network.model.DriveResponse;
import org.elastos.hive.vendors.onedrive.network.model.FileOrDirPropResponse;
import org.elastos.hive.vendors.connection.model.NoBodyEntity;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    Call<ResponseBody> read(@Header("Accept-Encoding") String acceptEncoding, @Path("path") String path);

    @PUT(OneDriveConstance.DRIVE+"/root:{path}:/content")
    Call<NoBodyEntity> write(@Path("path") String path, @Body RequestBody body);
}
