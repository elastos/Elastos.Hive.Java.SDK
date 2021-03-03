package org.elastos.hive.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class VaultServiceInfo extends Result<VaultServiceInfo> {
	@JsonProperty("did")
	private String userDid;
	@JsonProperty("max_storage")
	private long maxStorage;
	@JsonProperty("file_use_storage")
	private long fileUseStorage;
	@JsonProperty("db_use_storage")
	private float dbUseStorage;
	@JsonProperty("modify_time")
	private long modifyTime;
	@JsonProperty("start_time")
	private long startTime;
	@JsonProperty("end_time")
	private long endTime;
	@JsonProperty("pricing_using")
	private String pricingName;
	@JsonProperty("state")
	private String state;

	public String userDid() {
		return userDid;
	}

	public long maxStorage() {
		return maxStorage;
	}

	public long fileUseStorage() {
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

	public String pricingName() {
		return pricingName;
	}

	public String state() {
		return state;
	}

	public static VaultServiceInfo deserialize(String content) {
		return deserialize(content, VaultServiceInfo.class);
	}
}
