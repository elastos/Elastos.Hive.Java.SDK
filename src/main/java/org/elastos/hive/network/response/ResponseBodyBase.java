package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.exception.HiveException;
import retrofit2.Response;

public class ResponseBodyBase {
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

    /**
     * We can't check status on interceptor because of body string can be only read once.
     * @param response
     * @param <T>
     * @return
     * @throws HiveException
     */
    public static <T extends ResponseBodyBase> T validateBody(Response<T> response) throws HiveException {
        T body = response.body();
        if (body == null) throw new HiveException("Failed to get response body(null)");
        if (!SUCCESS.equals(body.getStatus())) {
            throw new HiveException("Status 'ERR' gotten from response body, code=" + body.getErrorCode() + ", message=" + body.getErrorMessage());
        }
        return body;
    }

    static class Error {
        @SerializedName("code")
        int code;
        @SerializedName("message")
        String message;
    }
}
