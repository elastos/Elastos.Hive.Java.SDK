package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ListChildrenResponse {
    @SerializedName("value")
    private List<FileInfo> items;

    public List<FileInfo> getItems() {
        return items;
    }
}
