package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeleteVaultsParams {
    @SerializedName("user_dids")
    private List<String> userDids;

    public DeleteVaultsParams(List<String> userDids) {
        this.userDids = userDids;
    }
}
