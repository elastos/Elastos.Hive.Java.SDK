package org.elastos.hive;

import org.elastos.hive.backup.ServiceBuilder;
import org.elastos.hive.service.PromotionService;

/**
 * This represents the service end-point of the backup hive node.
 *
 * <p>Currently, the backup hive node only supports store the backup data of the vault service,
 * 	and promote the backup node to the vault node. The old vault will be disabled
 * 	after this promotion.</p>
 *
 * <p>Before using promotion service, the subscription for the backup service is required on backup hive node.</p>
 *
 * <pre>
 *      BackupSubscription subscription = new BackupSubscription(appContext, providerAddress);
 *      subscription.subscribe().get();
 * </pre>
 *
 * <p>And then, execute the backup operation on the vault hive node.</p>
 *
 * <pre>
 *      Vault vault = new Vault(appContext, providerAddress);
 *      BackupService backupService = vault.getBackupService());
 *      backupService.startBackup().get();
 * </pre>
 *
 * <p>The third step is executing the promotion operation.</p>
 *
 * <pre>
 *      PromotionService promotionService = new Backup(appContext, providerAddress);
 *      promotionService.promote().get();
 * </pre>
 */
public class Backup extends ServiceEndpoint {
	private PromotionService promotionService;

	public Backup(AppContext context, String providerAddress) {
		super(context, providerAddress);
		this.promotionService = new ServiceBuilder(this).createPromotionService();
	}

	public Backup(AppContext context) {
		this(context, null);
	}

	public PromotionService getPromotionService() {
		return this.promotionService;
	}
}
