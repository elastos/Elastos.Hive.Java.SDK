package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class UsingPlan extends Result<UsingPlan> {

	@JsonProperty("vault_service_info")
	private UsingInfo usingPlan;

	public static class UsingInfo extends Result<UsingInfo> {
		@JsonProperty("max_storage")
		private int maxStorage;
		@JsonProperty("file_use_storage")
		private int fileUseStorage;
		@JsonProperty("db_use_storage")
		private int dbUseStorage;
		@JsonProperty("modify_time")
		private long modifyTime;
		@JsonProperty("start_time")
		private long startTime;
		@JsonProperty("end_time")
		private long endTime;
		@JsonProperty("pricing_using")
		private String pricingUsing;

		public int maxStorage() {
			return maxStorage;
		}

		public int fileUseStorage() {
			return fileUseStorage;
		}

		public int dbUseStorage() {
			return dbUseStorage;
		}

		public long modifyTime() {
			return modifyTime;
		}

		public long startTime() {
			return startTime;
		}

		public long endTime() {
			return endTime;
		}

		public String pricingUsing() {
			return pricingUsing;
		}

		public static UsingInfo deserialize(String content) {
			return deserialize(content, UsingInfo.class);
		}
	}
}
