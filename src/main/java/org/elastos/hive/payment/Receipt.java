package org.elastos.hive.payment;

import org.elastos.hive.Result;

public class Receipt extends Result<Receipt> {
	private String receiptId;
	private String orderId;
	private String customerDid;

	private String transId;
	private long transTime;
	private float transAmount;
	private float currency;

	private long createdTime;


	public String getReceiptId() {
		return receiptId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getCustomerDid() {
		return customerDid;
	}

	public String getTransId() {
		return transId;
	}

	public long getTransTime() {
		return transTime;
	}

	public float getTransAmount() {
		return transAmount;
	}

	public float getCurrency() {
		return currency;
	}

	public long getCreatedTime() {
		return createdTime;
	}
}
