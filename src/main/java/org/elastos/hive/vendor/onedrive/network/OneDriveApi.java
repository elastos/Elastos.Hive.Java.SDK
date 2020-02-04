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

package org.elastos.hive.vendor.onedrive.network;

import org.elastos.hive.vendor.connection.model.NoBodyEntity;
import org.elastos.hive.vendor.onedrive.network.model.DirChildrenResponse;
import org.elastos.hive.vendor.onedrive.network.model.FileOrDirPropResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OneDriveApi {
    @GET(ConnectConstance.APP_ROOT + ":/{path}")
    Call<FileOrDirPropResponse> getDirAndFileInfo(@Path("path") String path);

    @DELETE(ConnectConstance.APP_ROOT + ":{path}")
    Call<NoBodyEntity> deleteItem(@Path("path") String path);

    @GET(ConnectConstance.APP_ROOT + ":{path}:/children")
    Call<DirChildrenResponse> getChildren(@Path("path") String path);

    @GET(ConnectConstance.APP_ROOT + ":{path}:/content")
    Call<ResponseBody> read(@Header("Accept-Encoding") String acceptEncoding, @Path("path") String path);

    @PUT(ConnectConstance.APP_ROOT + ":{path}:/content")
    Call<NoBodyEntity> write(@Path("path") String path, @Body RequestBody body);
}
