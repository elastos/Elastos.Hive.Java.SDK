package org.elastos.hive.payment;

public class Order {
	private String orderHash;
	private String inAppDid;
	private String subscriberDid;

	private float payAmount;
	private String payCurrency;    	// ELA in default.

	private String pricingPlan;

	private long createdTime;
	private long expiredTime;

	private String signature;

	private PaymentStatus status;

	public enum PaymentStatus {
		Unpaid,
		Paid,
		Expired
	};

	public String getOrderId() {
		return orderHash;
	}

	public String getInAppDid() {
		return inAppDid;
	}

	public String getSubscriberDid() {
		return subscriberDid;
	}

	public float getPayAmount() {
		return payAmount;
	}

	public String getPayCurrency() {
		return payCurrency;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public String getPricingPlan() {
		return pricingPlan;
	}

	public String getSignature() {
		return signature;
	}

	public String getPaymentStatus() {
		return status.toString();
	}
}
