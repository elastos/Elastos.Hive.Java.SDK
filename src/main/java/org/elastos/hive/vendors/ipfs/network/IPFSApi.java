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

package org.elastos.hive.vendors.ipfs.network;

import org.elastos.hive.vendors.connection.model.NoBodyEntity;
import org.elastos.hive.vendors.ipfs.IPFSConstance;
import org.elastos.hive.vendors.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendors.ipfs.network.model.ListFileResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPFSApi {
    @Multipart
    @POST(IPFSConstance.ADD)
    Call<AddFileResponse> addFile(@Part MultipartBody.Part file);

    @POST(IPFSConstance.LS)//arg [string]: The path to the IPFS object(s) to list links from. Required: yes.
    Call<ListFileResponse> listFile(@Query(IPFSConstance.ARG) String ipfsObjPath);

    @POST(IPFSConstance.CAT)//arg [string]: The path to the IPFS object(s) to be outputted. Required: yes.
    Call<ResponseBody> catFile(@Query(IPFSConstance.ARG) String ipfsObjPath);

    @POST("http://{address}:{port}/version")
    Call<NoBodyEntity> version(@Path("address") String address,@Path("port")int port);
}
