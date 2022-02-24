package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

/**
 * This contains the details of the backup service.
 */
public class BackupInfo {
	@SerializedName("service_did")
	private String serviceDid;
	@SerializedName("storage_quota")
	private int storageQuota;
	@SerializedName("storage_used")
	private int storageUsed;
	@SerializedName("created")
	private double created;
	@SerializedName("updated")
	private double updated;
	@SerializedName("pricing_plan")
	private String pricingPlan;

	public void setServiceDid(String serviceDid) {
		this.serviceDid = serviceDid;
	}

	public void setStorageQuota(int storageQuota) {
		this.storageQuota = storageQuota;
	}

	public void setStorageUsed(int storageUsed) {
		this.storageUsed = storageUsed;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public void setPricingPlan(String pricingPlan) {
		this.pricingPlan = pricingPlan;
	}

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
		return (long)created;
	}

	public long getUpdated() {
		return (long)updated;
	}

	public String getPricingPlan() {
		return pricingPlan;
	}
}
