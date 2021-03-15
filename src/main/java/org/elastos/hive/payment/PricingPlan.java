package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class PricingPlan extends Result<PricingPlan> {
	private String planName;
	private int quota;
	private int serviceDays;
	private float payAmount;
	private String currency;

	@SuppressWarnings("unused")
	private String description;

	@JsonProperty("planName")
	public String getPlanName() {
		return planName;
	}

	@JsonProperty("quota")
	public int getStorageQuota() {
		return quota;
	}

	@JsonProperty("serviceDays")
	public int getServiceDays() {
		return serviceDays;
	}

	@JsonProperty("payAmount")
	public float getPayAmount() {
		return payAmount;
	}

	@JsonProperty("currency")
	public String getCurrency() {
		return currency;
	}
}
