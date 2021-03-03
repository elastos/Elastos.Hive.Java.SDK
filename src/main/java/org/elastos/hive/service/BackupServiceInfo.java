package org.elastos.hive.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class BackupServiceInfo extends Result<BackupServiceInfo> {
	@JsonProperty("did")
	private String userDid;
	@JsonProperty("backup_using")
	private String pricingName;
	@JsonProperty("max_storage")
	private long maxStorage;
	@JsonProperty("use_storage")
	private float useStorage;
	@JsonProperty("modify_time")
	private String modifyTime;
	@JsonProperty("start_time")
	private String startTime;
	@JsonProperty("end_time")
	private String endTime;

	public String userDid() {
		return userDid;
	}

	public String pricingName() {
		return pricingName;
	}

	public long maxStorage() {
		return maxStorage;
	}

	public float useStorage() {
		return useStorage;
	}

	public String modifyTime() {
		return modifyTime;
	}

	public String startTime() {
		return startTime;
	}

	public String endTime() {
		return endTime;
	}

	public static BackupServiceInfo deserialize(String content) {
		return deserialize(content, BackupServiceInfo.class);
	}
}
