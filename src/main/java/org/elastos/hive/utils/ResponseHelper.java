package org.elastos.hive.utils;

import org.elastos.hive.HiveException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseHelper {

    public static byte[] getBuffer(Response response){
        byte[] data = null;
        try {
            ResponseBody body = (ResponseBody) response.body();
            data = body.bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static long saveFileFromResponse(String storeFilepath , Response response) throws HiveException {
        ResponseBody body = (ResponseBody) response.body();
        FileOutputStream cacheStream = null;
        long total = 0;
        try {
            //write the data to the cache file.
            InputStream data = body.byteStream();

            cacheStream = new FileOutputStream(storeFilepath);
            byte[] b = new byte[1024];
            int length = 0;

            while((length = data.read(b)) > 0){
                cacheStream.write(b, 0, length);
                total += length;
            }

            data.close();
        } catch (Exception e) {
            throw new HiveException(e.getMessage());
        }
        finally {
            try {
                if (cacheStream != null) cacheStream.close();
                body.close();
            } catch (Exception e) {
                throw new HiveException(e.getMessage());
            }
        }

        return total;
    }
}
