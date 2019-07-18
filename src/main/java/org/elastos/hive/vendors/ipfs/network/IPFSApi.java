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

import org.elastos.hive.vendors.connection.Model.NoBodyEntity;
import org.elastos.hive.vendors.ipfs.IPFSConstance;
import org.elastos.hive.vendors.ipfs.network.model.ListChildResponse;
import org.elastos.hive.vendors.ipfs.network.model.PublishResponse;
import org.elastos.hive.vendors.ipfs.network.model.ResolveResponse;
import org.elastos.hive.vendors.ipfs.network.model.StatResponse;
import org.elastos.hive.vendors.ipfs.network.model.UIDResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IPFSApi {

    @POST(IPFSConstance.NEW)
    Call<UIDResponse> getNewUid();

    @GET(IPFSConstance.NAMERESOLVE)
    Call<ResolveResponse> resolve(@Query(IPFSConstance.ARG) String peerId);

//    @GET(IPFSConstance.NAMERESOLVE)
//    Call<ResolveResponse> resolve();

    @POST(IPFSConstance.UID_INFO)
    Call<UIDResponse> getUidInfo(@Query(IPFSConstance.UID) String uid);

    @POST(IPFSConstance.LOGIN)
    Call<NoBodyEntity> login(@Query(IPFSConstance.UID) String uid ,
                             @Query(IPFSConstance.HASH) String hash);

    @POST(IPFSConstance.PUBLISH)
    Call<PublishResponse> publish(@Query(IPFSConstance.UID) String uid ,
                                  @Query(IPFSConstance.PATH) String path);

    @POST(IPFSConstance.STAT)
    Call<StatResponse> getStat(@Query(IPFSConstance.UID) String uid ,
                               @Query(IPFSConstance.PATH) String path);

    @POST(IPFSConstance.MKDIR)
    Call<NoBodyEntity> mkdir(@Query(IPFSConstance.UID) String uid ,
                             @Query(IPFSConstance.PATH) String path ,
                             @Query(IPFSConstance.PARENTS) String parents);

    @POST(IPFSConstance.WRITE)
    Call<NoBodyEntity> createFile(@Query(IPFSConstance.UID) String uid ,
                                  @Query(IPFSConstance.PATH) String path ,
                                  @Query(IPFSConstance.CREATE) boolean create);

    @POST(IPFSConstance.MV)
    Call<NoBodyEntity> moveTo(@Query(IPFSConstance.UID) String uid ,
                              @Query(IPFSConstance.SOURCE) String source ,
                              @Query(IPFSConstance.DEST) String dest);

    @POST(IPFSConstance.CP)
    Call<NoBodyEntity> copyTo(@Query(IPFSConstance.UID) String uid ,
                              @Query(IPFSConstance.SOURCE) String source ,
                              @Query(IPFSConstance.DEST) String dest);

    @POST(IPFSConstance.RM)
    Call<NoBodyEntity> deleteItem(@Query(IPFSConstance.UID) String uid ,
                                  @Query(IPFSConstance.PATH) String path ,
                                  @Query(IPFSConstance.RESCURSIVE) String rescursive);

    @POST(IPFSConstance.LS)
    Call<ListChildResponse> list(@Query(IPFSConstance.UID) String uid ,
                                 @Query(IPFSConstance.PATH) String path);

    @POST(IPFSConstance.READ)
    Call<ResponseBody> read(@Query(IPFSConstance.UID) String uid ,
                                  @Query(IPFSConstance.PATH) String path);

    @POST(IPFSConstance.WRITE)
    Call<NoBodyEntity> write(@Query(IPFSConstance.UID) String uid ,
                                  @Query(IPFSConstance.PATH) String path ,
                                  @Query(IPFSConstance.CREATE) boolean create, 
                                  @Body RequestBody body);
}
