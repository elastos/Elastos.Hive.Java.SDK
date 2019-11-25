package org.elastos.hive.vendors.ipfs;


import org.elastos.hive.Callback;
import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.Length;

import java.util.concurrent.CompletableFuture;

public interface IHiveIPFS{
    CompletableFuture<CID> putFile(String absPath, boolean encrypt);
    CompletableFuture<CID> putFile(String absPath, boolean encrypt , Callback<CID> callback);

    CompletableFuture<CID> putFileFromBuffer(byte[] data, boolean encrypt);
    CompletableFuture<CID> putFileFromBuffer(byte[] data, boolean encrypt , Callback<CID> callback);

    CompletableFuture<Length> getFileLength(CID cid);
    CompletableFuture<Length> getFileLength(CID cid , Callback<Length> callback);

    CompletableFuture<Data> getFileToBuffer(CID cid, boolean decrypt);
    CompletableFuture<Data> getFileToBuffer(CID cid, boolean decrypt , Callback<Data> callback);

    CompletableFuture<Length> getFile(CID cid , boolean decrypt, String storeAbsPath);
    CompletableFuture<Length> getFile(CID cid , boolean decrypt, String storeAbsPath , Callback<Length> callback);
}


