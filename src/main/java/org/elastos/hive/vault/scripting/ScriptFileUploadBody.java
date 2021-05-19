package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class ScriptFileUploadBody {
    @SerializedName("path")
    private String path;

    public ScriptFileUploadBody(String path) {
        this.path = path;
    }
}
