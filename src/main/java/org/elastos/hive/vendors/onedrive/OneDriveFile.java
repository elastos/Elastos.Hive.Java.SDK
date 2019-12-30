package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Callback;
import org.elastos.hive.HiveError;
import org.elastos.hive.HiveException;
import org.elastos.hive.HiveFile;
import org.elastos.hive.result.Data;
import org.elastos.hive.result.FileList;
import org.elastos.hive.result.Length;
import org.elastos.hive.result.ValueList;
import org.elastos.hive.result.Void;
import org.elastos.hive.utils.DigitalUtil;
import org.elastos.hive.utils.HeaderUtil;
import org.elastos.hive.utils.ResponseHelper;
import org.elastos.hive.vendors.connection.ConnectionManager;
import org.elastos.hive.vendors.onedrive.network.OneDriveApi;
import org.elastos.hive.vendors.onedrive.network.model.DirChildrenResponse;
import org.elastos.hive.vendors.onedrive.network.model.FileOrDirPropResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class OneDriveFile extends HiveFile implements IHiveFile {
    private String filename ;
    private AuthHelper authHelper;
    OneDriveFile(String filename , AuthHelper authHelper){
        this.filename = filename ;
        this.authHelper = authHelper ;
    }

    @Override
    public CompletableFuture<Void> putFile(String destFilename, String sorceFilename, boolean encrypt) {
        return authHelper.checkExpired()
                .thenCompose(result ->writeToBackend(destFilename, sorceFilename,null , null));
    }

    @Override
    public CompletableFuture<Void> putFile(String destFilename, String sorceFilename, boolean encrypt, org.elastos.hive.Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result ->writeToBackend(destFilename, sorceFilename,null, callback));
    }

    @Override
    public CompletableFuture<Void> putFileFromBuffer(String destFilename, byte[] data, boolean encrypt) {
        return authHelper.checkExpired()
                .thenCompose(result ->writeToBackend(destFilename, null ,data , null));
    }

    @Override
    public CompletableFuture<Void> putFileFromBuffer(String destFilename, byte[] data, boolean encrypt, org.elastos.hive.Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result ->writeToBackend(destFilename, null ,data , callback));
    }

    @Override
    public CompletableFuture<Length> getFileLength(String filename) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetFileLength(filename , null));
    }

    @Override
    public CompletableFuture<Length> getFileLength(String filename, org.elastos.hive.Callback<Length> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetFileLength(filename , callback));
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(String filename, boolean decrypt) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetBuffer(filename , null));
    }

    @Override
    public CompletableFuture<Data> getFileToBuffer(String filename, boolean decrypt, org.elastos.hive.Callback<Data> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetBuffer(filename , callback));
    }

    @Override
    public CompletableFuture<Length> getFile(String filename, boolean decrypt, String storePath) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetFile(filename , storePath , null));
    }

    @Override
    public CompletableFuture<Length> getFile(String filename, boolean decrypt, String storePath, org.elastos.hive.Callback<Length> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetFile(filename , storePath , callback));
    }

    @Override
    public CompletableFuture<Void> deleteFile(String filename) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(filename , null)) ;
    }

    @Override
    public CompletableFuture<Void> deleteFile(String filename, org.elastos.hive.Callback callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(filename , callback)) ;
    }

    @Override
    public CompletableFuture<FileList> listFile() {
        return authHelper.checkExpired()
                .thenCompose(result -> doListFile(null));
    }

    @Override
    public CompletableFuture<FileList> listFile(org.elastos.hive.Callback<FileList> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doListFile(callback));
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, boolean encrypt) {
        return authHelper.checkExpired()
                .thenCompose(result -> doPutValue(key , value , null));
    }

    @Override
    public CompletableFuture<Void> putValue(String key, byte[] value, boolean encrypt, org.elastos.hive.Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doPutValue(key , value , callback));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, boolean encrypt) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(key,null))
                .thenCompose(result -> doMergeLengthAndData(value)
                .thenCompose(data -> writeToBackend(key, null ,data , null)));
    }

    @Override
    public CompletableFuture<Void> setValue(String key, byte[] value, boolean encrypt, org.elastos.hive.Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(key,null))
                .thenCompose(result -> doMergeLengthAndData(value)
                .thenCompose(data -> writeToBackend(key, null ,data , callback)));
    }

    @Override
    public CompletableFuture<ValueList> getValue(String key, boolean decrypt) {
        return authHelper.checkExpired()
                .thenCompose(result -> doGetValue(key,decrypt,null));
    }

    @Override
    public CompletableFuture<ValueList> getValue(String key, boolean decrypt
            , Callback<ValueList> callback) {
        return authHelper.checkExpired()
                .thenCompose(result ->doGetValue(key,decrypt,callback));
    }

    @Override
    public CompletableFuture<Void> deleteValueFromKey(String key) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(key,null));
    }

    @Override
    public CompletableFuture<Void> deleteValueFromKey(String key , org.elastos.hive.Callback<Void> callback) {
        return authHelper.checkExpired()
                .thenCompose(result -> doDeleteFile(key,callback));
    }

    private CompletableFuture<ValueList> doGetValue(String key , boolean decrypt , Callback<ValueList> callback){
        CompletableFuture<ValueList> future = new CompletableFuture<>();
        ArrayList<Data> arrayList = new ArrayList<>();
        try {
            CompletableFuture<Data> innerFuture = doGetBuffer(key, new Callback<Data>() {
                @Override
                public void onError(HiveException e) {
                    if (callback != null) callback.onError(e);
                    future.completeExceptionally(e);
                }

                @Override
                public void onSuccess(Data body) {
                }
            });
            Data data = innerFuture.get();
            if (innerFuture.isCompletedExceptionally()) return future;

            createValueResult(arrayList , data.getData());

            ValueList valueList = new ValueList(arrayList);
            if (callback != null) callback.onSuccess(valueList);
            future.complete(valueList);
        } catch (Exception e) {
            e.printStackTrace();
            HiveException exception = new HiveException(HiveError.GET_VALUE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private String creatDestFilePath(String destFileName){
        if (!filename.equals("/"))
        return filename +"/"+ destFileName;
        else return filename+destFileName;
    }

    private CompletableFuture<Void> writeToBackend(String destFilename , String pathname , byte[] data , org.elastos.hive.Callback<Void> callback){
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (pathname == null && data == null){
            HiveException exception = new HiveException(HiveError.PUT_FILE_ERROR);
            if (callback!=null) callback.onError(exception);
            future.completeExceptionally(exception);
            return future;
        }
        String destFilePath = creatDestFilePath(destFilename);
        RequestBody requestBody = null ;
        if (pathname != null){
            File file = new File(pathname);
            requestBody = createWriteRequestBody(file);
        }else {
            requestBody = createWriteRequestBody(data);
        }
        try {
            Response response = ConnectionManager.getOnedriveApi()
                    .write(destFilePath, requestBody)
                    .execute();
            int checkResponseCode = checkResponseCode(response) ;
            if (checkResponseCode == 0){
                Void result = new Void();
                if (callback!=null) callback.onSuccess(result);
                future.complete(result);
            }else if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
            }else{
                HiveException exception = new HiveException(HiveError.PUT_FILE_ERROR);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveError.PUT_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private RequestBody createWriteRequestBody(File file){
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }

    private RequestBody createWriteRequestBody(byte[] data){
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
    }

    private CompletableFuture<Length> doGetFileLength(String filename , org.elastos.hive.Callback<Length> callback){
        CompletableFuture<Length> future = new CompletableFuture() ;
        try {
            String destFilePath = creatDestFilePath(filename);
            Response response = requestFileInfo(destFilePath);
            int checkResponseCode = checkResponseCode(response) ;
            if (checkResponseCode == 0){
                int fileLength = decodeFileLength(response);
                Length length = new Length(fileLength);
                if (callback!=null) callback.onSuccess(length);
                future.complete(length);
            }else if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }else{
                HiveException exception = new HiveException(HiveError.GET_FILE_LENGTH_ERROR);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
        } catch (Exception e) {
            HiveException exception = new HiveException(HiveError.GET_FILE_LENGTH_ERROR);
            if (callback!=null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private CompletableFuture<Void> doDeleteFile(String filename , org.elastos.hive.Callback callback){
        CompletableFuture<Void> future = new CompletableFuture<>();
        String destFilePath = creatDestFilePath(filename);
        try {
            Response response = ConnectionManager.getOnedriveApi()
                    .deleteItem(destFilePath)
                    .execute();
            int checkResponseCode = checkResponseCode(response) ;
            if (checkResponseCode == 0){
                Void result = new Void();
                if (callback != null) callback.onSuccess(result);
                future.complete(result);
            }else if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }else{
                HiveException hiveException = new HiveException(HiveError.DEL_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }
        } catch (Exception ex) {
            HiveException exception = new HiveException(HiveError.DEL_FILE_ERROR);
            if (callback != null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private CompletableFuture<Length> doGetFile(String filename , String storePath , org.elastos.hive.Callback callback){
        CompletableFuture<Length> future = new CompletableFuture<>();

        try {
            checkFileExist(storePath);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveError.FILE_ALREADY_EXIST_ERROR);
            callback.onError(exception);
            future.completeExceptionally(exception);
            e.printStackTrace();
        }

        try {
            Response response = getFileOrBuffer(filename);
            int checkResponseCode = checkResponseCode(response);
            if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }else if (checkResponseCode!=0 ||
                    (HeaderUtil.getContentLength(response) == -1
                    && !HeaderUtil.isTrunced(response))){
                HiveException hiveException = new HiveException(HiveError.DEL_FILE_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }
            long total = ResponseHelper.saveFileFromResponse(storePath , response);
            Length lengthObj = new Length(total);
            if (callback!=null) callback.onSuccess(lengthObj);
            future.complete(lengthObj);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveError.GET_FILE_ERROR);
            if (callback!=null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private CompletableFuture<Data> doGetBuffer(String fileName , org.elastos.hive.Callback<Data> callback){
        CompletableFuture<Data> future = new CompletableFuture<>();
        try {
            Response response = getFileOrBuffer(fileName);

            int checkResponseCode = checkResponseCode(response);
            if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }else if(checkResponseCode != 0){
                HiveException hiveException = new HiveException(HiveError.GET_BUFFER_ERROR);
                if (callback != null) callback.onError(hiveException);
                future.completeExceptionally(hiveException);
                return future;
            }

            byte[] bytes = ResponseHelper.getBuffer(response);
            if (bytes == null){
                Data data = new Data(new byte[0]);
                if (callback!=null) callback.onSuccess(data);
                future.complete(data);
            }
            Data data = new Data(bytes);
            if (callback!=null) callback.onSuccess(data);
            future.complete(data);
        } catch (HiveException e) {
            HiveException exception = new HiveException(HiveError.GET_BUFFER_ERROR);
            if (callback!=null) callback.onError(exception);
            future.completeExceptionally(exception);
        }
        return future;
    }

    private void checkFileExist(String storePath) throws HiveException{
        if (storePath!=null){
            File cacheFile = new File(storePath);
            if (cacheFile.exists() && cacheFile.length() > 0){
                throw new HiveException("File already exist");
            }
        }else{
            throw new HiveException("Store filePath is null");
        }
    }

    private Response getFileOrBuffer(String filename) throws HiveException {
        Response response ;
        String destFilePath = creatDestFilePath(filename);
        try {
            response = ConnectionManager.getOnedriveApi()
                    .read("identity",destFilePath)
                    .execute();

        } catch (Exception ex) {
            throw new HiveException(ex.getMessage());
        }
        return response;
    }

    private CompletableFuture<FileList> doListFile(Callback<FileList> callback){
        CompletableFuture future = new CompletableFuture();
        try {
            OneDriveApi api = ConnectionManager.getOnedriveApi();
            Response<DirChildrenResponse> response ;
            if (this.filename.equals("/")) response = api.getRootChildren().execute();
            else response = api.getChildren(this.filename).execute();
            int checkResponseCode = checkResponseCode(response) ;
            if(checkResponseCode == 404){
                HiveException exception = new HiveException(HiveError.ITEM_NOT_FOUND);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }else if (checkResponseCode != 0){
                HiveException exception = new HiveException(HiveError.LIST_FILE_ERROR);
                if (callback!=null) callback.onError(exception);
                future.completeExceptionally(exception);
                return future;
            }
            String[] result = decodeListFile(response);
            FileList fileList = new FileList(result);
            if (callback!=null) callback.onSuccess(fileList);
            future.complete(fileList);
        } catch (Exception ex) {
            HiveException e = new HiveException(ex.getMessage());
            future.completeExceptionally(e);
        }
        return future;
    }

    private CompletableFuture<Void> doPutValue(String key, byte[] value , org.elastos.hive.Callback callback){
        byte[] originData = new byte[0];
        try {
            originData = doGetBuffer(key,null).get().getData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        byte[] data = mergeLengthAndData(value);

        byte[] finalData = mergeData(originData,data);

        try {
            checkFileExistInBackEnd(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeToBackend(key,null , finalData , callback);
    }

    private String[] decodeListFile(Response response){
        DirChildrenResponse dirChildrenResponse = (DirChildrenResponse) response.body();
        List<DirChildrenResponse.ValueBean> list = dirChildrenResponse.getValue();
        String[] beans = new String[list.size()];
        for (int i =0 ; i<beans.length ; i++){
            beans[i] = list.get(i).getName();
        }
        return beans;
    }

    private int decodeFileLength(Response response){
        FileOrDirPropResponse info = (FileOrDirPropResponse) response.body();
        return info.getSize();
    }


    private CompletableFuture<byte[]> doMergeLengthAndData(byte[] data){
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        byte[] result = mergeLengthAndData(data);
        future.complete(result);
        return future;
    }

    private byte[] mergeLengthAndData(byte[] data){
        byte[] lengthByte = DigitalUtil.intToByteArray(data.length);
        return mergeData(lengthByte,data);
    }

    private byte[] mergeData(byte[] bytes1 , byte[] bytes2){
        byte[] tmp = new byte[bytes1.length+bytes2.length];
        System.arraycopy(bytes1, 0, tmp, 0, bytes1.length);
        System.arraycopy(bytes2, 0, tmp, bytes1.length, bytes2.length);
        return tmp ;
    }

    private byte[] spliteBytes(byte[] data , int startPos , int length){
        byte[] tmp = new byte[length];
        System.arraycopy(data,startPos,tmp,0,length);

        return tmp ;
    }

    private int calcuLength(byte[] data , int startPos){
        int length ;
        byte[] lengthByte = new byte[4];

        System.arraycopy(data, startPos,lengthByte,0,4);

        return DigitalUtil.byteArrayToInt(lengthByte);
    }

    private void createValueResult(ArrayList<Data> arrayList , byte[] data){
        int total = data.length;
        int dataLength = calcuLength(data,0);
        byte[] strbytes = spliteBytes(data , 4,dataLength);
        arrayList.add(new Data(strbytes));
        int remainingDataLength = total - (dataLength+4);
        if (remainingDataLength<=0){
            return ;
        }else{
            byte[] remainingData = new byte[remainingDataLength];
            System.arraycopy(data , dataLength+4 , remainingData , 0 , remainingDataLength);
            createValueResult(arrayList,remainingData);
        }
    }

    private void checkFileExistInBackEnd(String filename) throws Exception {
        String destFilePath = creatDestFilePath(filename);
        Response response = requestFileInfo(destFilePath);
    }

    private Response requestFileInfo(String filename) throws Exception {
        Response response = ConnectionManager.getOnedriveApi()
                .getDirAndFileInfo(filename)
                .execute();
        return response;
    }

    private  int checkResponseCode(Response response){
        if (response == null) return -1;
        int code = response.code();
        switch (code){
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
                return 0 ;
            default:
                return code;
        }
    }
}
