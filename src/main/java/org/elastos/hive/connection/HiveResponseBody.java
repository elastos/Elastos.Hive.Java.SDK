package org.elastos.hive.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class HiveResponseBody {
    private static final String SUCCESS = "OK";

    @SerializedName("_status")
    private String status;

    @SerializedName("_error")
    private Error error;

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

    public static <T> T getRequestStream(HttpURLConnection connection, Class<T> resultType) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        if (resultType.isAssignableFrom(OutputStream.class)) {
            UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
            return resultType.cast(uploader);
        } else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
            OutputStreamWriter writer = new UploadOutputStreamWriter(connection, outputStream);
            return resultType.cast(writer);
        } else {
            throw new InvalidPropertiesFormatException("Not supported result type: " + resultType.getName());
        }
    }

    public static <T> T getResponseStream(Response<ResponseBody> response, Class<T> resultType) {
        ResponseBody body = response.body();
        if (body == null)
            throw new RuntimeException("Failed to get response body");

        if (resultType.isAssignableFrom(Reader.class))
            return resultType.cast(new InputStreamReader(body.byteStream()));
        else if (resultType.isAssignableFrom(InputStream.class))
            return resultType.cast(body.byteStream());
        else
            throw new IllegalArgumentException("Not supported result type");
    }

    public static KeyValueDict jsonNode2KeyValueDic(JsonNode node) {
        return new ObjectMapper().convertValue(node, new TypeReference<KeyValueDict>() {});
    }

    public static List<KeyValueDict> jsonNodeList2KeyValueDicList(List<JsonNode> docs) {
        return docs.stream().map(HiveResponseBody::jsonNode2KeyValueDic).collect(Collectors.toList());
    }

    public static JsonNode KeyValueDict2JsonNode(KeyValueDict dict) {
        return new ObjectMapper().convertValue(dict, JsonNode.class);
    }

    public static List<JsonNode> KeyValueDictList2JsonNodeList(List<KeyValueDict> dicts) {
        return dicts.stream().map(HiveResponseBody::KeyValueDict2JsonNode).collect(Collectors.toList());
    }

    static class Error {
        @SerializedName("code")
        int code;
        @SerializedName("message")
        String message;
    }
}
