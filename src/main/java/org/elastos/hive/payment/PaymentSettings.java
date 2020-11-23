package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class PaymentSettings extends Result<PaymentSettings> {
	@JsonProperty("receivingELAAddress")
	private String receivingELAAddress;

	public String receivingELAAddress() {
		return receivingELAAddress;
	}
}
