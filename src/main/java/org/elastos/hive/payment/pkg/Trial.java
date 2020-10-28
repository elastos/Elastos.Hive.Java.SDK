package org.elastos.hive.payment.pkg;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class Trial extends Result<Trial> {
	@JsonProperty("freeDays")
	private int freeDays;
	@JsonProperty("maxStorage")
	private int maxStorage;
	@JsonProperty("deleteIfUnpaidAfterDays")
	private int deleteIfUnpaidAfterDays;
	@JsonProperty("canReadIfUnpaid")
	private boolean canReadIfUnpaid;

	public int freeDays() {
		return freeDays;
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

	public static Trial deserialize(String content) {
		return deserialize(content, Trial.class);
	}
}
