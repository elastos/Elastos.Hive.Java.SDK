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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ResponseHelper {

    public static byte[] getBuffer(Response response) {
        byte[] data = null;
        try {
            ResponseBody body = (ResponseBody) response.body();
            data = body != null ? body.bytes() : new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<JsonNode> getArray(Response response, String key) {
        List<JsonNode> rets = new ArrayList<>();
        rets.clear();
        try {
            String json = toString(response);
            JsonNode arrNode = new ObjectMapper().readTree(json).get(key);
            if (arrNode.isArray()) {
                for (JsonNode objNode : arrNode) {
                    rets.add(objNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rets;
    }

    public static <T> T getValue(String json, Class<T> clz) {
        if(null==json) return null;

        Object obj = null;
        try {
            if(clz.isAssignableFrom(String.class)) {
                obj = json;
            } else if(clz.isAssignableFrom(byte[].class)) {
                obj = json.getBytes();
            } else if(clz.isAssignableFrom(JsonNode.class)) {
                obj = new ObjectMapper().readTree(json);
            } else if(clz.isAssignableFrom(Reader.class)) {
                obj = new StringReader(json);
            } else {
                obj = new ObjectMapper().readValue(json, clz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    public static <T> T getValue(Response response, Class<T> clz) throws IOException {
        String json = toString(response);
        return getValue(json, clz);
    }

    public static String toString(Response response) throws IOException {
        ResponseBody body = (ResponseBody) response.body();
        return body != null ? body.string() : "";
    }

    public static long writeOutput(Response response, OutputStream outputStream) throws IOException {
        ResponseBody body = (ResponseBody) response.body();
        InputStream inputStream = body != null ? body.byteStream() : null;
        int length = 0;
        int ch;
        if (inputStream == null) return length;
        while ((ch = inputStream.read()) != -1) {
            length++;
            outputStream.write(ch);
        }
        return length;
    }

    public static Reader getToReader(Response response) {
        ResponseBody body = (ResponseBody) response.body();
        InputStream inputStream = body != null ? body.byteStream() : null;
        if (inputStream == null) return null;
        return new InputStreamReader(inputStream);
    }

    public static InputStream getInputStream(Response response) {
        ResponseBody body = (ResponseBody) response.body();
        InputStream inputStream = body != null ? body.byteStream() : null;
        if (inputStream == null) return null;
        return inputStream;
    }

}
