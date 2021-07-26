package org.elastos.hive;

import org.elastos.hive.service.*;
import org.elastos.hive.vault.ServiceBuilder;

/**
 * This class explicitly represents the vault service subscribed by "userDid".
 *
 * <p>To use the vault, subscription is required.</p>
 *
 * <pre>
 *      VaultSubscription subscription = new VaultSubscription(appContext, providerAddress);
 *      subscription.subscribe().get();
 * </pre>
 *
 * <p>Then the services belongs to the vault will be got by this class.</p>
 *
 * <pre>
 *      Vault vault = new Vault(appContext, providerAddress);
 *      FilesService filesService = vault.getFilesService();
 * </pre>
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private DatabaseService database;
	private ScriptingService scripting;
	private BackupService 	backupService;

	public Vault(AppContext context, String providerAddress) {
		super(context, providerAddress);

		ServiceBuilder builder = new ServiceBuilder(this);
		this.filesService	= builder.createFilesService();
		this.database		= builder.createDatabase();
		this.scripting	 	= builder.createScriptingService();
		this.backupService  = builder.createBackupService();
	}

	/**
	 * Get the files service of the vault.
	 *
	 * @return The instance of the files service.
	 */
	public FilesService getFilesService() {
		return this.filesService;
	}

	/**
	 * Get the database service of the vault.
	 *
	 * @return The instance of the database service.
	 */
	public DatabaseService getDatabaseService() {
		return this.database;
	}

	/**
	 * Get the scripting service of the vault.
	 *
	 * @return The instance of the scripting service.
	 */
	public ScriptingService getScriptingService() {
		return this.scripting;
	}

	/**
	 * Get the backup service of the vault.
	 *
	 * @return The instance of the backup service.
	 */
	public BackupService getBackupService() {
		return this.backupService;
	}
}
