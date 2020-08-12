package org.elastos.hive.vendor.vault.network.model;

public class UploadResponse extends BaseResponse {
    private String upload_file_url;

    public String getUpload_file_url() {
        return upload_file_url;
    }

    public void setUpload_file_url(String upload_file_url) {
        this.upload_file_url = upload_file_url;
    }
}
