package org.elastos.hive.vault.files;

import java.util.List;

import org.elastos.hive.network.model.FileInfo;

import com.google.gson.annotations.SerializedName;

class FileInfoList {
	@SerializedName("file_info_list")
    private List<FileInfo> fileInfoList;

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

}
