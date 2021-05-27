package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;

class UpdateResult {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
