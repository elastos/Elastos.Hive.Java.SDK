package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.payment.Order;

public class OrderInfoResponseBody extends HiveResponseBody {
    @SerializedName("order_info")
    private Order orderInfo;

    public Order getOrderInfo() {
        return this.orderInfo;
    }
}
