package org.elastos.hive.payment.pkg;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class Price extends Result<Price> {
	@JsonProperty("price_name")
	private String priceName;
	@JsonProperty("amount")
	private float amount;
	@JsonProperty("serviceDays")
	private int serviceDays;
	@JsonProperty("currency")
	private String currency;

	public String priceName(){
		return priceName;
	}

	public float amount() {
		return amount;
	}

	public int serviceDays() {
		return serviceDays;
	}

	public String currency() {
		return currency;
	}

	public static Price deserialize(String content) {
		return deserialize(content, Price.class);
	}
}
