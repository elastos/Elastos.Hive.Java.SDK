package org.elastos.hive.vault.files;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class FileInfoList extends HiveResponseBody {
	@SerializedName("file_info_list")
    private List<FileInfo> fileInfoList;

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

}
