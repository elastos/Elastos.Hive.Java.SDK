package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

class OrderInfoResponseBody extends HiveResponseBody {
    @SerializedName("order_info")
    private Order orderInfo;

    public Order getOrderInfo() {
        return this.orderInfo;
    }
}
