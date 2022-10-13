package org.elastos.hive.subscription;

import com.google.gson.annotations.SerializedName;

/**
 * This contains the details of the vault service.
 */
public class VaultInfo {
	@SerializedName("service_did")
	private String serviceDid;
	@SerializedName("storage_quota")
	private long storageQuota;
	@SerializedName("storage_used")
	private long storageUsed;
	@SerializedName("created")
	private long created;
	@SerializedName("updated")
	private long updated;
	@SerializedName("pricing_plan")
	private String pricingPlan;
	@SerializedName("app_count")
	private int appCount;
	@SerializedName("access_count")
	private int accessCount;
	@SerializedName("access_amount")
	private long accessAmount;
	@SerializedName("access_last_time")
	private long accessLastTime;

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

	public long getStorageQuota() {
		return storageQuota;
	}

	public long getStorageUsed() {
		return storageUsed;
	}

	public long getCreated() {
		return created;
	}

	public long getUpdated() {
		return updated;
	}

	public String getPricingPlan() {
		return pricingPlan;
	}

	public void setAppCount(int appCount) {
		this.appCount = appCount;
	}

	public int getAppCount() {
		return this.appCount;
	}

	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	public long getAccessAmount() {
		return accessAmount;
	}

	public void setAccessAmount(long accessAmount) {
		this.accessAmount = accessAmount;
	}

	public long getAccessLastTime() {
		return accessLastTime;
	}

	public void setAccessLastTime(long accessLastTime) {
		this.accessLastTime = accessLastTime;
	}
}
