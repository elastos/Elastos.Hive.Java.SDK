package org.elastos.hive;

import org.elastos.hive.backup.ServiceBuilder;
import org.elastos.hive.service.PromotionService;

public class Backup extends ServiceEndpoint {
	private PromotionService promotionService;

	public Backup(AppContext context, String userDid, String providerAddress) {
		super(context, userDid, providerAddress, userDid, null);
		this.promotionService = new ServiceBuilder(this).createPromotionService();
	}

	public PromotionService getPromotionService() {
		return this.promotionService;
	}
}
