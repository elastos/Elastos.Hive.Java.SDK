package org.elastos.hive.provider;

import com.google.gson.annotations.SerializedName;

public class FilledOrderDetail {
    @SerializedName("order_id")
    private String orderId;
    @SerializedName("receipt_id")
    private String receiptId;
    @SerializedName("user_did")
    private String userDid;
    @SerializedName("subscription")
    private String subscription;
    @SerializedName("pricing_name")
    private String pricingName;
    @SerializedName("ela_amount")
    private double elaAmount;
    @SerializedName("ela_address")
    private String elaAddress;
    @SerializedName("paid_did")
    private String paidDid;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getPricingName() {
        return pricingName;
    }

    public void setPricingName(String pricingName) {
        this.pricingName = pricingName;
    }

    public double getElaAmount() {
        return elaAmount;
    }

    public void setElaAmount(double elaAmount) {
        this.elaAmount = elaAmount;
    }

    public String getElaAddress() {
        return elaAddress;
    }

    public void setElaAddress(String elaAddress) {
        this.elaAddress = elaAddress;
    }

    public String getPaidDid() {
        return paidDid;
    }

    public void setPaidDid(String paidDid) {
        this.paidDid = paidDid;
    }
}
