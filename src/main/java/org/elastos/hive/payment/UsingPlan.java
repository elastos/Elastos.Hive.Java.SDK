package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class UsingPlan extends Result<UsingPlan> {
	@JsonProperty("did")
	private String did;
	@JsonProperty("max_storage")
	private long maxStorage;
	@JsonProperty("file_use_storage")
	private int fileUseStorage;
	@JsonProperty("db_use_storage")
	private float dbUseStorage;
	@JsonProperty("modify_time")
	private float modifyTime;
	@JsonProperty("start_time")
	private float startTime;
	@JsonProperty("end_time")
	private float endTime;
	@JsonProperty("pricing_using")
	private String pricingUsing;

	public String did() {
		return did;
	}

	public long maxStorage() {
		return maxStorage;
	}

	public int fileUseStorage() {
		return fileUseStorage;
	}

	public float dbUseStorage() {
		return dbUseStorage;
	}

	public float modifyTime() {
		return modifyTime;
	}

	public float startTime() {
		return startTime;
	}

	public float endTime() {
		return endTime;
	}

	public String pricingUsing() {
		return pricingUsing;
	}

	public static UsingPlan deserialize(String content) {
		return deserialize(content, UsingPlan.class);
	}
}
