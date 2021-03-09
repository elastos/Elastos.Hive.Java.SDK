package org.elastos.hive;

import org.elastos.hive.backup.ServiceBuilder;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.PromotionService;

public class Backup extends ServiceEndpoint {
	private PromotionService promotionService;

	public Backup(AppContext context, String userDid, String providerAddress) throws HiveException {
		super(context, providerAddress, userDid, userDid, null);

		this.promotionService = new ServiceBuilder(this).createPromotionService();
	}

	/*
	class BackupInfo {

	}

	public CompletableFuture<BackupInfo> getMetaInfo() {
		// TODO;
		return null;
	}
	*/

	public PromotionService getPromotionService() {
		return this.promotionService;
	}
}
