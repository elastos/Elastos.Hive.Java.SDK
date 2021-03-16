package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.FileInfo;

import java.util.List;

public class FilesListResponseBody extends ResponseBodyBase {

    @SerializedName("file_info_list")
    private List<FileInfo> fileInfoList;

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

}
