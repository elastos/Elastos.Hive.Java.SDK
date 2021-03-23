package org.elastos.hive.network.model;

import com.google.gson.annotations.SerializedName;

public class ScriptFileUploadBody {
    @SerializedName("path")
    private String path;

    public ScriptFileUploadBody(String path) {
        this.path = path;
    }
}
