package org.elastos.hive;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.utils.DateUtil;

class BackupCredential extends Result<BackupCredential> {
	@JsonProperty("issuanceDate")
	private String issuanceDate;
	@JsonProperty("expirationDate")
	private String expirationDate;

	public boolean isExpired() {
		long currentSeconds = System.currentTimeMillis() / 1000;
		return currentSeconds >= getExpiredTime();
	}

	public long getExpiredTime() {
		return DateUtil.getTime(expirationDate);
	}

	public long getIssuanceTime() {
		return DateUtil.getTime(issuanceDate);
	}

	public static BackupCredential deserialize(String content) {
		return deserialize(content, BackupCredential.class);
	}
}
