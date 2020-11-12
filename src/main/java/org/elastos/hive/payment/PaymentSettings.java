package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class PaymentSettings extends Result<PaymentSettings> {
	@JsonProperty("receivingELAAddress")
	private String receivingELAAddress;
	@JsonProperty("wait_payment_timeout")
	private int waitPaymentTimeout;
	@JsonProperty("wait_tx_timeout")
	private int waitTxTimeout;

	public String receivingELAAddress() {
		return receivingELAAddress;
	}

	public int waitPaymentTimeout() {
		return waitPaymentTimeout;
	}

	public int waitTxTimeout() {
		return waitTxTimeout;
	}
}
