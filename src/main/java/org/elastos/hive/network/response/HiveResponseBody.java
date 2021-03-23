package org.elastos.hive.network.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.ResponseBody;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.network.model.UploadOutputStream;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Map;

public class HiveResponseBody {
    private static final String SUCCESS = "OK";

    @SerializedName("_status")
    private String status;

    @SerializedName("_error")
    private Error error;

    public static Map<String, Object> jsonNode2Map(JsonNode node) {
        return new ObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {});
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getErrorCode() {
        return error == null ? -1 : error.code;
    }

    public String getErrorMessage() {
        return error == null ? "" : error.message;
    }

    public boolean failed() {
        return !SUCCESS.equals(this.status);
    }

    /**
     * We can't check status on interceptor because of body string can be only read once.
     * @param response
     * @param <T>
     * @return
     * @throws HiveException
     */
    public static <T extends HiveResponseBody> T validateBody(Response<T> response) throws HiveException {
        T body = response.body();
        if (body == null)
            throw new HiveException("Failed to get response body(null)");
        if (body.failed())
            throw new HiveException("Status 'ERR' gotten from response body, code=" + body.getErrorCode() + ", message=" + body.getErrorMessage());
        return body;
    }

    public static String validateBodyStr(Response<ResponseBody> response) throws HiveException {
        ResponseBody body = response.body();
        if (body == null)
            throw new HiveException("Failed to get body on validateBody");
        try {
            String bodyStr = body.string();
            if (new Gson().fromJson(bodyStr, HiveResponseBody.class).failed()) {
                throw new HiveException("Get ERR response status on validateBody");
            }
            return bodyStr;
        } catch (IOException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public static <T> T getValue(String json, Class<T> clz) {
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
            throw new HiveSdkException("unsupported result type for call script.");
        }
        return (T) obj;
    }

    public static <T> T getRequestStream(HttpURLConnection connection, Class<T> resultType) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        if (resultType.isAssignableFrom(OutputStream.class)) {
            UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
            return resultType.cast(uploader);
        } else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            return resultType.cast(writer);
        } else {
            throw new HiveSdkException("Not supported result type: " + resultType.getName());
        }
    }

    public static <T> T getResponseStream(Response<ResponseBody> response, Class<T> resultType) throws HiveException {
        ResponseBody body = response.body();
        if (body == null)
            throw new HiveException("Failed to get response body");
        if (resultType.isAssignableFrom(Reader.class)) {
            return resultType.cast(new InputStreamReader(body.byteStream()));
        } else if (resultType.isAssignableFrom(InputStream.class)) {
            return resultType.cast(body.byteStream());
        } else {
            throw new HiveSdkException("Not supported result type");
        }
    }

    static class Error {
        @SerializedName("code")
        int code;
        @SerializedName("message")
        String message;
    }
}
