package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

/**
 * The receipt contains the details of the paid information.
 */
public class Receipt {
	@SerializedName("receipt_id")
	private String receiptId;
	@SerializedName("order_id")
	private Integer orderId;
	@SerializedName("subscription")
	private String subscription;
	@SerializedName("pricing_plan")
	private String pricingPlan;
	@SerializedName("payment_amount")
	private Float paymentAmount;
	@SerializedName("paid_did")
	private String paidDid;
	@SerializedName("create_time")
	private Integer createTime;
	@SerializedName("receiving_address")
	private String receivingAddress;
	@SerializedName("receipt_proof")
	private String receiptProof;

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

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

	public Float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getPaidDid() {
		return paidDid;
	}

	public void setPaidDid(String paidDid) {
		this.paidDid = paidDid;
	}

	public Integer getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}

	public String getReceivingAddress() {
		return receivingAddress;
	}

	public void setReceivingAddress(String receivingAddress) {
		this.receivingAddress = receivingAddress;
	}

	public String getReceiptProof() {
		return receiptProof;
	}

	public void setReceiptProof(String receiptProof) {
		this.receiptProof = receiptProof;
	}
}
