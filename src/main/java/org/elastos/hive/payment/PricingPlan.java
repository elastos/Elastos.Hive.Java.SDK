package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class PricingPlan extends Result<PricingPlan> {
	@JsonProperty("name")
	private String name;
	@JsonProperty("maxStorage")
	private int maxStorage;
	@JsonProperty("maxNetworkSpeed")
	private int maxNetworkSpeed;
	@JsonProperty("deleteIfUnpaidAfterDays")
	private int deleteIfUnpaidAfterDays;
	@JsonProperty("canReadIfUnpaid")
	private boolean canReadIfUnpaid;
	@JsonProperty("pricing")
	private List<Price> pricing;

	public String name() {
		return name;
	}

	public int maxStorage() {
		return maxStorage;
	}

	public int maxNetworkSpeed() {
		return maxNetworkSpeed;
	}

	public int deleteIfUnpaidAfterDays() {
		return deleteIfUnpaidAfterDays;
	}

	public boolean canReadIfUnpaid() {
		return canReadIfUnpaid;
	}

	public List<Price> pricing() {
		return pricing;
	}

	public static PricingPlan deserialize(String content) {
		return deserialize(content, PricingPlan.class);
	}

	static class Price extends Result<Price> {
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
}
