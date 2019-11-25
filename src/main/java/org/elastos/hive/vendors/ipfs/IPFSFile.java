package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.Callback;
import org.elastos.hive.HiveError;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.result.CID;
import org.elastos.hive.result.Data;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.ipfs.network.model.AddFileResponse;
import org.elastos.hive.vendors.ipfs.network.model.ListFileResponse;
import org.elastos.hive.result.Length;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;


public class IPFSFile extends HiveFile implements IHiveIPFS {
    IPFSRpc ipfsRpc ;
    IPFSFile(IPFSRpc ipfsRpc){
        this.ipfsRpc = ipfsRpc ;
    }

    private CompletableFuture<CID> doPutFile(String sorceFilename , Callback<CID> callback){
        CompletableFuture future = new CompletableFuture() ;
        if (!checkConnection(future)) return future;
        try {
            MultipartBody.Part requestBody = createFileRequestBody(sorceFilename);
            Response response = ConnectionManager.getIPFSApi().addFile(requestBody).execute();
            if (response == null || response.code() != 200){
                HiveException exception = new HiveException(HiveError.PUT_FILE_ERROR);
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
            HiveException exception = new HiveException(HiveError.PUT_FILE_ERROR);
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
                HiveException exception = new HiveException(HiveError.PUT_BUFFER_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
            AddFileResponse addFileResponse = (AddFileResponse) response.body();
            CID cid = new CID(addFileResponse.getHash());
            if (callback!=null) callback.onSuccess(cid);
            future.complete(cid);
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveError.PUT_BUFFER_ERROR) ;
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
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
            future.completeExceptionally(new HiveException(HiveError.GET_FILE_LENGTH_ERROR));
        }
        if (response == null || response.code() != 200){
            HiveException exception = new HiveException(HiveError.GET_FILE_LENGTH_ERROR);
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
            HiveException exception = new HiveException(HiveError.GET_FILE_LENGTH_ERROR);
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
                HiveException exception = new HiveException(HiveError.GET_FILE_ERROR);
                if (callback != null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future ;
            }
            long length = ResponseHelper.saveFileFromResponse(storeFilepath,response);
            Length lengthObj = new Length(length);
            if (callback!=null) callback.onSuccess(lengthObj);
            future.complete(lengthObj);
        } catch (Exception e) {
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

                future.completeExceptionally(new HiveException(HiveError.GET_FILE_ERROR));
            }
        } catch (Exception e) {
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
            future.completeExceptionally(new HiveException(HiveError.NO_RPC_NODE_AVAILABLE));
            return false;
        }
        return true ;
    }

    @Override
    public CompletableFuture<CID> putFile(String absPath, boolean encrypt) {
        return doPutFile(absPath , null);
    }

    @Override
    public CompletableFuture<CID> putFile(String absPath, boolean encrypt, Callback<CID> callback) {
        return doPutFile(absPath , callback);
    }

    @Override
    public CompletableFuture<CID> putFileFromBuffer(byte[] data, boolean encrypt) {
        return doPutBuffer(data , null);
    }

    @Override
    public CompletableFuture<CID> putFileFromBuffer(byte[] data, boolean encrypt, Callback<CID> callback) {
        return doPutBuffer(data , callback);
    }

    @Override
    public CompletableFuture<Length> getFileLength(CID cid) {
        return doGetFileLength(cid , null);
    }

    @Override
    public CompletableFuture<Length> getFileLength(CID cid, Callback<Length> callback) {
        return doGetFileLength(cid , callback);
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(CID cid, boolean decrypt) {
        return doGetBuffer(cid , null);
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(CID cid, boolean decrypt, Callback<Data> callback) {
        return doGetBuffer(cid , callback);
    }

    @Override
    public CompletableFuture<Length> getFile(CID cid, boolean decrypt, String storeAbsPath) {
        return doCatFile(cid,storeAbsPath , null);
    }

    @Override
    public CompletableFuture<Length> getFile(CID cid, boolean decrypt, String storeAbsPath, Callback<Length> callback) {
        return doCatFile(cid,storeAbsPath , callback);
    }
}
