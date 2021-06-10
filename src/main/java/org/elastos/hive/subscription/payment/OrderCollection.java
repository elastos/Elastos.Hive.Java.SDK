package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class OrderCollection {
    @SerializedName("value")
    private List<Order> orders;

    public List<Order> orderList() {
        return orders;
    }
}
