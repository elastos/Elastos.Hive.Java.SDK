package org.elastos.hive.payment.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class PackageInfo extends Result<PackageInfo> {
	@JsonProperty("name")
	private String name;
	@JsonProperty("maxStorage")
	private int maxStorage;
	@JsonProperty("deleteIfUnpaidAfterDays")
	private int deleteIfUnpaidAfterDays;
	@JsonProperty("canReadIfUnpaid")
	private boolean canReadIfUnpaid;
	@JsonProperty("price_name")
	private String priceName;
	@JsonProperty("amount")
	private int amount;
	@JsonProperty("serviceDays")
	private int serviceDays;
	@JsonProperty("currency")
	private String currency;

	public String name() {
		return name;
	}

	public int maxStorage() {
		return maxStorage;
	}

	public int deleteIfUnpaidAfterDays() {
		return deleteIfUnpaidAfterDays;
	}

	public boolean canReadIfUnpaid() {
		return canReadIfUnpaid;
	}

	public String priceName() {
		return priceName;
	}

	public int amount() {
		return amount;
	}

	public int serviceDays() {
		return serviceDays;
	}

	public String currency() {
		return currency;
	}

	public static PackageInfo deserialize(String content) {
		return deserialize(content, PackageInfo.class);
	}
}
