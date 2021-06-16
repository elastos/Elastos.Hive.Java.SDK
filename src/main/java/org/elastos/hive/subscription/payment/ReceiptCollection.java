package org.elastos.hive.subscription.payment;

import java.util.List;

import com.google.gson.annotations.SerializedName;

class ReceiptCollection {
	@SerializedName("value")
	private List<Receipt> receipts;

	public List<Receipt> receiptList() {
		return receipts;
	}
}
