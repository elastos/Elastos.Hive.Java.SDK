package org.elastos.hive.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class BackupUsingPlan extends Result<BackupUsingPlan> {
	@JsonProperty("did")
	private String did;
	@JsonProperty("backup_using")
	private String name;
	@JsonProperty("max_storage")
	private String maxStorage;
	@JsonProperty("use_storage")
	private String useStorage;
	@JsonProperty("modify_time")
	private String modifyTime;
	@JsonProperty("start_time")
	private String startTime;
	@JsonProperty("end_time")
	private String endTime;
}
