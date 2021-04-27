package org.elastos.hive.network.responseV2;

import com.google.gson.annotations.SerializedName;

public class ErrorResponseBody {
    @SerializedName("error")
    private Error error;

    public Error getError() {
        return error;
    }

    public class Error {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
