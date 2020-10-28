package org.elastos.hive.payment.pkg;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class Package extends Result<Package> {
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

	public static Package deserialize(String content) {
		return deserialize(content, Package.class);
	}
}
