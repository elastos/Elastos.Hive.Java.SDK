package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class PaymentsInfo {
    @SerializedName("payments")
    private List<PaymentDetail> payments;

    List<PaymentDetail> getPayments() {
        return payments;
    }
}
