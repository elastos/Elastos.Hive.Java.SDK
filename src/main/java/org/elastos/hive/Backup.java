package org.elastos.hive;

import org.elastos.hive.backup.ServiceBuilder;
import org.elastos.hive.service.PromotionService;

/**
 * This represents the service end-point of the backup hive node.
 *
 * Currently, the backup hive node only supports store the backup data of the vault service,
 * 	and promote the backup node to the vault node. The old vault will be disabled
 * 	after this promotion.
 *
 * Before using promotion service, the subscription for the backup service is required on backup hive node.
 *
 * 		BackupSubscription subscription = new BackupSubscription(appContext, providerAddress);
 * 		subscription.subscribe().get();
 *
 * And then, execute the backup operation on the vault hive node.
 *
 *		Vault vault = new Vault(appContext, providerAddress);
 *		BackupService backupService = vault.getBackupService());
 *		backupService.startBackup().get();
 *
 * The third step is executing the promotion operation.
 *
 *  	PromotionService promotionService = new Backup(appContext, providerAddress);
 *  	promotionService.promote().get();
 */
public class Backup extends ServiceEndpoint {
	private PromotionService promotionService;

	public Backup(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.promotionService = new ServiceBuilder(this).createPromotionService();
	}

	public PromotionService getPromotionService() {
		return this.promotionService;
	}
}
