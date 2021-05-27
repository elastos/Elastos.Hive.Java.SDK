package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;

class UpdatedResult {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
