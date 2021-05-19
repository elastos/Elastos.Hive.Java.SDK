package org.elastos.hive.vault.payment;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.vault.payment.Order;

class OrderInfoResponseBody extends HiveResponseBody {
    @SerializedName("order_info")
    private Order orderInfo;

    public Order getOrderInfo() {
        return this.orderInfo;
    }
}
