package org.elastos.hive.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class BackupUsingPlan extends Result<BackupUsingPlan> {
	@JsonProperty("did")
	private String did;
	@JsonProperty("backup_using")
	private String name;
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

	public String did() {
		return did;
	}

	public String name() {
		return name;
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

	public String dndTime() {
		return endTime;
	}

	public static BackupUsingPlan deserialize(String content) {
		return deserialize(content, BackupUsingPlan.class);
	}
}
