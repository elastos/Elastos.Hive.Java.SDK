package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayOrderRequestBody {
    @SerializedName("order_id")
    private String orderId;
    @SerializedName("pay_txids")
    private List<String> payTxids;

    public PayOrderRequestBody setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PayOrderRequestBody setPayTxids(List<String> payTxids) {
        this.payTxids = payTxids;
        return this;
    }
}
