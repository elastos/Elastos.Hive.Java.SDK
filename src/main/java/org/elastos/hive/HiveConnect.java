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

package org.elastos.hive;

import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;

import java.util.concurrent.CompletableFuture;

public interface HiveConnect extends Connect, HiveFile, IPFSFile{
}

interface Connect{
    void connect(Authenticator authenticator);
    void disConnect();
    void setEncryptKey(String encryptKey);
}

interface HiveFile{
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

interface IPFSFile{
    CompletableFuture<CID> putIPFSFile(String absPath, boolean encrypt);
    CompletableFuture<CID> putIPFSFile(String absPath, boolean encrypt , Callback<CID> callback);

    CompletableFuture<CID> putIPFSFileFromBuffer(byte[] data, boolean encrypt);
    CompletableFuture<CID> putIPFSFileFromBuffer(byte[] data, boolean encrypt , Callback<CID> callback);

    CompletableFuture<Length> getIPFSFileLength(CID cid);
    CompletableFuture<Length> getIPFSFileLength(CID cid , Callback<Length> callback);

    CompletableFuture<Data> getIPFSFileToBuffer(CID cid, boolean decrypt);
    CompletableFuture<Data> getIPFSFileToBuffer(CID cid, boolean decrypt , Callback<Data> callback);

    CompletableFuture<Length> getIPFSFile(CID cid , boolean decrypt, String storeAbsPath);
    CompletableFuture<Length> getIPFSFile(CID cid , boolean decrypt, String storeAbsPath , Callback<Length> callback);
}
