package org.elastos.hive.vendor.vault.network.model;

import org.elastos.hive.file.FileInfo;

import java.util.List;

public class FilesResponse extends BaseResponse {

    private List<FileInfo> file_info_list;

    public List<FileInfo> getFiles() {
        return file_info_list;
    }
}
