package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class PricingPlan extends Result<PricingPlan> {
	@JsonProperty("name")
	private String name;
	@JsonProperty("maxStorage")
	private int maxStorage;
	@JsonProperty("serviceDays")
	private int serviceDays;
	@JsonProperty("amount")
	private float amount;
	@JsonProperty("currency")
	private boolean currency;
	@JsonProperty("paymentSettings")
	private PaymentSettings paymentSettings;

	public String name() {
		return name;
	}

	public int maxStorage() {
		return maxStorage;
	}

	public int serviceDays() {
		return serviceDays;
	}

	public float amount() {
		return amount;
	}

	public boolean currency() {
		return currency;
	}

	public static PricingPlan deserialize(String content) {
		return deserialize(content, PricingPlan.class);
	}

	public static class PaymentSettings extends Result<PaymentSettings> {
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

		public static PaymentSettings deserialize(String content) {
			return deserialize(content, PaymentSettings.class);
		}

	}

}
