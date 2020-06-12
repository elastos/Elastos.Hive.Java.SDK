package org.elastos.hive.vendor.hivevault.network.model;

import java.util.List;

public class FilesResponse extends BaseResponse {

    private List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
