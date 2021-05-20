package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class PaymentCreateResponseBody extends HiveResponseBody {
    @SerializedName("order_id")
    private String orderId;

    public String getOrderId() {
        return this.orderId;
    }
}
