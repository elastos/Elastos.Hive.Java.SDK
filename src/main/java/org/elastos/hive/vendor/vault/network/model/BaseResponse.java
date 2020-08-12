package org.elastos.hive.vendor.vault.network.model;

public class BaseResponse {
    private String _status;
    private Error _error;

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public Error get_error() {
        return _error;
    }

    public void set_error(Error _error) {
        this._error = _error;
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

    }
}
