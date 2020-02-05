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

package org.elastos.hive.utils;

import org.elastos.hive.exception.HiveException;

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
