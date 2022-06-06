package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

/**
 * The order is used for payment module and represents and order to upgrade the service of the vault or the backup.
 */
public class Order {
	// Only for getOrders()
	@SerializedName("order_id")
	private Integer orderId;
	private String subscription;
	@SerializedName("pricing_plan")
	private String pricingPlan;
	@SerializedName("paying_did")
	private String payingDid;
	@SerializedName("payment_amount")
	private Float paymentAmount;
	@SerializedName("create_time")
	private Integer createTime;
	@SerializedName("expiration_time")
	private Integer expirationTime;
	@SerializedName("receiving_address")
	private String receivingAddress;
	private String proof;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public String getPricingPlan() {
		return pricingPlan;
	}

	public void setPricingPlan(String pricingPlan) {
		this.pricingPlan = pricingPlan;
	}

	public String getPayingDid() {
		return payingDid;
	}

	public void setPayingDid(String payingDid) {
		this.payingDid = payingDid;
	}

	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Integer getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}

	public Integer getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Integer expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getReceivingAddress() {
		return receivingAddress;
	}

	public void setReceivingAddress(String receivingAddress) {
		this.receivingAddress = receivingAddress;
	}

	public String getProof() {
		return proof;
	}

	public void setProof(String proof) {
		this.proof = proof;
	}
}
