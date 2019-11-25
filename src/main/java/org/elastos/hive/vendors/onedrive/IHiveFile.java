package org.elastos.hive.vendors.onedrive;


import org.elastos.hive.Callback;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface IHiveFile {
    CompletableFuture<Void> putFile(String destFilename , String sorceFilename , boolean encrypt);
    CompletableFuture<Void> putFile(String destFilename , String sorceFilename , boolean encrypt , Callback<Void> callback);

    CompletableFuture<Void> putFileFromBuffer(String destFilename , byte[] data , boolean encrypt);
    CompletableFuture<Void> putFileFromBuffer(String destFilename , byte[] data , boolean encrypt , Callback<Void> callback);

    CompletableFuture<Length> getFileLength(String filename);
    CompletableFuture<Length> getFileLength(String filename , Callback<Length> callback);

    CompletableFuture<Data> getFileToBuffer(String filename , boolean decrypt);
    CompletableFuture<Data> getFileToBuffer(String filename , boolean decrypt , Callback<Data> callback);

    CompletableFuture<Length> getFile(String filename , boolean decrypt , String storePath);
    CompletableFuture<Length> getFile(String filename , boolean decrypt , String storePath , Callback<Length> callback);

    CompletableFuture<Void> deleteFile(String filename);
    CompletableFuture<Void> deleteFile(String filename , Callback callback);

    CompletableFuture<FileList> listFile();
    CompletableFuture<FileList> listFile(Callback<FileList> callback);

    CompletableFuture<Void> putValue(String key , byte[] value , boolean encrypt);
    CompletableFuture<Void> putValue(String key , byte[] value , boolean encrypt , Callback<Void> callback);


    CompletableFuture<Void> setValue(String key , byte[] value , boolean encrypt);
    CompletableFuture<Void> setValue(String key , byte[] value , boolean encrypt , Callback<Void> callback);

    CompletableFuture<ValueList> getValue(String key , boolean decrypt);
    CompletableFuture<ValueList> getValue(String key , boolean decrypt , Callback<ValueList> callback);

    CompletableFuture<Void> deleteValueFromKey(String key);
    CompletableFuture<Void> deleteValueFromKey(String key , Callback<Void> callback);
}
