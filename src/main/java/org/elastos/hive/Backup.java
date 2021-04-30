package org.elastos.hive;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.elastos.hive.backup.ServiceBuilder;
import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.service.PromotionService;

public class Backup extends ServiceEndpoint {
	private PromotionService promotionService;

	public class PropertySet {
		private String serviceDid;
		private String pricingPlan;
		private long created;
		private long updated;
		private long quota;
		private long used;

		public String getServiceId() {
			return serviceDid;
		}

		public String getPricingPlan() {
			return pricingPlan;
		}

		public Date getCreated() {
			return new Date(created);
		}

		public Date getLastUpdated() {
			return new Date(updated);
		}

		public long getQuotaSpace() {
			return quota;
		}

		public long getUsedSpace() {
			return used;
		}

		PropertySet setServiceId(String serviceId) {
			this.serviceDid = serviceId;
			return this;
		}

		PropertySet setPricingPlan(String pricingPlan) {
			this.pricingPlan = pricingPlan;
			return this;
		}

		PropertySet setCreated(long created) {
			this.created = created;
			return this;
		}

		PropertySet setUpdated(long updated) {
			this.updated = updated;
			return this;
		}

		PropertySet setQuota(long quota) {
			this.quota = quota;
			return this;
		}

		PropertySet setUsedSpace(long used) {
			this.used = used;
			return this;
		}
	};

	public Backup(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.promotionService = new ServiceBuilder(this).createPromotionService();
	}

	public PromotionService getPromotionService() {
		return this.promotionService;
	}

	public CompletableFuture<PropertySet> getPropertSet() {
		throw new UnsupportedMethodException();
	}
}
