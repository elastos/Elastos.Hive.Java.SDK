package org.elastos.hive.provider;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class VaultStats {
    @SerializedName("vaults")
    private List<VaultDetail> vaults;

    List<VaultDetail> getVaults() {
        return vaults;
    }
}
