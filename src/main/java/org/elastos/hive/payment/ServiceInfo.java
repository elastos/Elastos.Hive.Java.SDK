package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class ServiceInfo extends Result<ServiceInfo> {
	@JsonProperty("max_storage")
	private int maxStorage;
	@JsonProperty("start_time")
	private long startTime;
	@JsonProperty("end_time")
	private long endTime;
	@JsonProperty("delete_time")
	private long deleteTime;
	@JsonProperty("can_read_if_unpaid")
	private boolean canRead;
	@JsonProperty("state")
	private String state;

	public int maxStorage() {
		return maxStorage;
	}

	public long startTime() {
		return startTime;
	}

	public long endTime() {
		return endTime;
	}

	public long deleteTime() {
		return deleteTime;
	}

	public boolean canRead() {
		return canRead;
	}

	public String state() {
		return state;
	}

	public static ServiceInfo deserialize(String content) {
		return deserialize(content, ServiceInfo.class);
	}
}
