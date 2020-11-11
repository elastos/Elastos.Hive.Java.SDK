package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

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
	private String currency;

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

	public String currency() {
		return currency;
	}

	public static PricingPlan deserialize(String content) {
		return deserialize(content, PricingPlan.class);
	}
}
