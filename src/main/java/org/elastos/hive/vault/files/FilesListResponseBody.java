package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

public class FilesListResponseBody extends HiveResponseBody {

    @SerializedName("file_info_list")
    private List<FileInfo> fileInfoList;

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

}
