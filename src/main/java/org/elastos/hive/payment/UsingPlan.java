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
	private long modifyTime;
	@JsonProperty("start_time")
	private long startTime;
	@JsonProperty("end_time")
	private long endTime;
	@JsonProperty("pricing_using")
	private String name;

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

	public String name() {
		return name;
	}

	public static UsingPlan deserialize(String content) {
		return deserialize(content, UsingPlan.class);
	}
}
