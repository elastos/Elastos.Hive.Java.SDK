package org.elastos.hive.vendor.vault.network.model;

public abstract class BaseResponse {
    private String _status;
    private Error _error;

    private static class Error {
        private String code;
        private String message;
    }
}
