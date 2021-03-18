package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class FilesMoveRequestBody {
    @SerializedName("src_path")
    private String srcPath;

    @SerializedName("dst_path")
    private String dstPath;

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setDstPath(String dstPath) {
        this.dstPath = dstPath;
    }
}
