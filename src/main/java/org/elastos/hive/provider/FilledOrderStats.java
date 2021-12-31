package org.elastos.hive.provider;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class FilledOrderStats {
    @SerializedName("payments")
    private List<FilledOrderDetail> payments;

    List<FilledOrderDetail> getPayments() {
        return payments;
    }
}
