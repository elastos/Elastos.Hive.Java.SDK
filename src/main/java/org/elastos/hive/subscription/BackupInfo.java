package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

public class BackupInfo {
	@SerializedName("service_did")
	private String serviceDid;
	@SerializedName("storage_quota")
	private int storageQuota;
	@SerializedName("storage_used")
	private int storageUsed;
	@SerializedName("created")
	private long created;
	@SerializedName("updated")
	private long updated;
	@SerializedName("price_plan")
	private String pricePlan;

	public String getServiceDid() {
		return serviceDid;
	}

	public int getStorageQuota() {
		return storageQuota;
	}

	public int getStorageUsed() {
		return storageUsed;
	}

	public long getCreated() {
		return created;
	}

	public long getUpdated() {
		return updated;
	}

	public String getPricePlan() {
		return pricePlan;
	}
}
