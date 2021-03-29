package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;

public class PaymentCreateResponseBody extends HiveResponseBody {
    @SerializedName("order_id")
    private String orderId;

    public String getOrderId() {
        return this.orderId;
    }
}
