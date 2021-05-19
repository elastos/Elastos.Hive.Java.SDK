package org.elastos.hive.vault.payment;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.vault.payment.Order;

import java.util.List;

class OrderListResponseBody extends HiveResponseBody {
    @SerializedName("order_info_list")
    private List<Order> orderInfoList;

    public List<Order> getOrderInfoList() {
        return this.orderInfoList;
    }
}
