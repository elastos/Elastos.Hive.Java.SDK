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

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.ConnectOptions;
import org.elastos.hive.HiveConnect;
import org.elastos.hive.HiveException;
import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendors.ipfs.network.model.ListFileResponse;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class IPFSConnect implements HiveConnect{
    private IPFSRpc ipfsRpc ;

    public IPFSConnect(ConnectOptions connectOptions){
        IPFSConnectOptions options = (IPFSConnectOptions)connectOptions;
        ipfsRpc = new IPFSRpc(options.getRpcNodes());
    }

    @Override
    public void connect(Authenticator authenticator){
        try {
            ipfsRpc.checkReachable();
        } catch (HiveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
    }

    @Override
    public void setEncryptKey(String encryptKey) {
    }

    @Override
    public CompletableFuture<Void> putFile(String destFilename, String sorceFilename, boolean encrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> putFile(String destFilename, String sorceFilename, boolean encrypt, Callback<Void> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> putFileFromBuffer(String destFilename, byte[] data, boolean encrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> putFileFromBuffer(String destFilename, byte[] data, boolean encrypt, Callback<Void> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Length> getFileLength(String filename) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Length> getFileLength(String filename, Callback<Length> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(String filename, boolean decrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(String filename, boolean decrypt, Callback<Data> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Length> getFile(String filename, boolean decrypt, String storePath) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Length> getFile(String filename, boolean decrypt, String storePath, Callback<Length> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> deleteFile(String filename) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> deleteFile(String filename, Callback callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<FileList> listFile() {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<FileList> listFile(Callback<FileList> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, boolean encrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, boolean encrypt, Callback<Void> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, boolean encrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, boolean encrypt, Callback<Void> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<ValueList> getValue(String key, boolean decrypt) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<ValueList> getValue(String key, boolean decrypt, Callback<ValueList> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> deleteValueFromKey(String key) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<Void> deleteValueFromKey(String key, Callback<Void> callback) {
        return unSupportFunction();
    }

    @Override
    public CompletableFuture<CID> putIPFSFile(String absPath, boolean encrypt) {
        return doPutFile(absPath , null);
    }

    @Override
    public CompletableFuture<CID> putIPFSFile(String absPath, boolean encrypt, Callback<CID> callback) {
        return doPutFile(absPath , callback);
    }

    @Override
    public CompletableFuture<CID> putIPFSFileFromBuffer(byte[] data, boolean encrypt) {
        return doPutBuffer(data , null);
    }

    @Override
    public CompletableFuture<CID> putIPFSFileFromBuffer(byte[] data, boolean encrypt, Callback<CID> callback) {
        return doPutBuffer(data , callback);
    }

    @Override
    public CompletableFuture<Length> getIPFSFileLength(CID cid) {
        return doGetFileLength(cid , null);
    }

    @Override
    public CompletableFuture<Length> getIPFSFileLength(CID cid, Callback<Length> callback) {
        return doGetFileLength(cid , callback);
    }

    @Override
    public CompletableFuture<Data> getIPFSFileToBuffer(CID cid, boolean decrypt) {
        return doGetBuffer(cid , null);
    }

    @Override
    public CompletableFuture<Data> getIPFSFileToBuffer(CID cid, boolean decrypt, Callback<Data> callback) {
        return doGetBuffer(cid , callback);
    }

    @Override
    public CompletableFuture<Length> getIPFSFile(CID cid, boolean decrypt, String storeAbsPath) {
        return doCatFile(cid,storeAbsPath , null);
    }

    @Override
    public CompletableFuture<Length> getIPFSFile(CID cid, boolean decrypt, String storeAbsPath, Callback<Length> callback) {
        return doCatFile(cid,storeAbsPath , callback);
    }

    private CompletableFuture<CID> doPutFile(String sorceFilename , Callback<CID> callback){
        CompletableFuture future = new CompletableFuture() ;
        if (!checkConnection(future)) return future;
        try {
            MultipartBody.Part requestBody = createFileRequestBody(sorceFilename);
            Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
            if (response == null || response.code() != 200){
                HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
            AddFileResponse addFileResponse = (AddFileResponse) response.body();
            CID cid = new CID(addFileResponse.getHash());
            if (callback != null) callback.onSuccess(cid);
            future.complete(cid);
        } catch (Exception e) {
            e.printStackTrace();
            HiveException exception = new HiveException(HiveException.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private CompletableFuture<CID> doPutBuffer(byte[] data , Callback<CID> callback){
        CompletableFuture future = new CompletableFuture();
        if (!checkConnection(future)) return future;
        try {
            MultipartBody.Part requestBody = createBufferRequestBody(data);
            Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
            if (response == null || response.code() != 200){
                HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
            AddFileResponse addFileResponse = (AddFileResponse) response.body();
            CID cid = new CID(addFileResponse.getHash());
            if (callback!=null) callback.onSuccess(cid);
            future.complete(cid);
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveException.PUT_BUFFER_ERROR) ;
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }

        return future;
    }

    private MultipartBody.Part createFileRequestBody(String sorceFilename){
        if (sorceFilename == null || sorceFilename.equals("")){
            return null ;
        }
        File file = new File(sorceFilename);
        RequestBody requestFile = RequestBody.create(null, file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return body ;
    }

    private MultipartBody.Part createBufferRequestBody(byte[] data){
        if (data == null || data.length == 0){
            return null ;
        }
        RequestBody requestFile = RequestBody.create(null, data);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "data", requestFile);
        return body ;
    }

    private CompletableFuture<Length> doGetFileLength(CID cid ,Callback<Length> callback){
        CompletableFuture<Length> future = new CompletableFuture();
        if (!checkConnection(future)) return future;
        String hash ;
        int size = 0;
        Response response = null;
        try {
            response = ConnectionManager.getIPFSApi().listFile(cid.getCid()).execute();
        } catch (Exception e) {
            future.completeExceptionally(new HiveException(HiveException.GET_FILE_LENGTH_ERROR));
        }
        if (response == null || response.code() != 200){
            HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
            if (callback!=null) callback.onError(exception);
            future.completeExceptionally(exception);
            return future;
        }
        ListFileResponse listFileResponse = (ListFileResponse) response.body();

        HashMap<String, ListFileResponse.ObjectsBean.Bean> map = listFileResponse.getObjects();

        if (null!=map && map.size()>0){
            for (String key:map.keySet()){
                hash = map.get(key).getHash();
                size = map.get(key).getSize();
                break;//if result only one
            }
            Length length = new Length(size);
            if (callback!=null) callback.onSuccess(length);
            future.complete(length);
        }else{
            HiveException exception = new HiveException(HiveException.GET_FILE_LENGTH_ERROR);
            callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private CompletableFuture<Length> doCatFile(CID cid,String storeFilepath ,Callback<Length> callback){
        CompletableFuture<Length> future = new CompletableFuture() ;
        if (!checkConnection(future)) return future;
        try {
            Response response = getFileOrBuffer(cid.getCid());
            if (response == null || response.code()!=200){
                HiveException exception = new HiveException(HiveException.GET_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future ;
            }
            long length = ResponseHelper.saveFileFromResponse(storeFilepath,response);
            Length lengthObj = new Length(length);
            if (callback!=null) callback.onSuccess(lengthObj);
            future.complete(lengthObj);
        } catch (Exception e) {
            HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
            if (callback != null) callback.onError(hiveException);
            future.completeExceptionally(hiveException);
            e.printStackTrace();
        }
        return future;
    }

    private CompletableFuture<Data> doGetBuffer(CID cid , Callback<Data> callback){
        CompletableFuture<Data> future = new CompletableFuture();
        if (!checkConnection(future)) return future;
        try {
            Response response = getFileOrBuffer(cid.getCid());
            if (response!=null){
                byte[] buffer = ResponseHelper.getBuffer(response);
                Data data = new Data(buffer);
                if (callback != null) callback.onSuccess(data);
                future.complete(data);
            }else{
                HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
                if (callback!=null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
            }
        } catch (Exception e) {
            HiveException hiveException = new HiveException(HiveException.GET_FILE_ERROR);
            if (callback!=null) callback.onError(hiveException);
            future.completeExceptionally(hiveException);
            e.printStackTrace();
        }
        return future;
    }

    private Response getFileOrBuffer(String cid) throws HiveException {
        Response response ;
        try {
            response = ConnectionManager.getIPFSApi()
                    .catFile(cid)
                    .execute();
        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
    }

    private boolean checkConnection(CompletableFuture future){
        if (!ipfsRpc.isAvailable()){
            future.completeExceptionally(new HiveException(HiveException.NO_RPC_NODE_AVAILABLE));
            return false;
        }
        return true ;
    }

    private CompletableFuture unSupportFunction(){
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(new HiveException(HiveException.UNSUPPORT_FUNCTION));
        return completableFuture;
    }
}
