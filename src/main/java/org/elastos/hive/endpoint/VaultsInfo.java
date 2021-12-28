package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class VaultsInfo {
    @SerializedName("vaults")
    private List<VaultDetail> vaults;

    List<VaultDetail> getVaults() {
        return vaults;
    }
}
