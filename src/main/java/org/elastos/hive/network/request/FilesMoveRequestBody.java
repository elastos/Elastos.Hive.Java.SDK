package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class FilesMoveRequestBody {
    @SerializedName("src_path")
    private final String srcPath;

    @SerializedName("dst_path")
    private final String dstPath;

    public FilesMoveRequestBody(String srcPath, String dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }
}
