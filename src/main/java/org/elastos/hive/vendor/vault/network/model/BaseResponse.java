package org.elastos.hive.vendor.vault.network.model;

public class BaseResponse {
    private String _status;
    private Error _error;

    public String get_status() {
        return _status;
    }

    public Error get_error() {
        return _error;
    }

    public static class Error {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public String toString() {
            return "{\"code\":"+code+"\",\"message\":\""+message+"\"}";
        }
    }
}
