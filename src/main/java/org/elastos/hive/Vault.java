package org.elastos.hive;

import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.service.*;
import org.elastos.hive.vault.ServiceBuilder;

/**
 * This class explicitly represents the vault service subscribed by "userDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private BackupService 	backupService;

	public Vault(AppContext context, String providerAddress) {
		super(context, providerAddress);

		this.filesService 	= new ServiceBuilder(this).createFilesService();
		this.databaseService = new ServiceBuilder(this).createDatabase();
		this.backupService 	= new ServiceBuilder(this).createBackupService();
		this.scriptingService = new ServiceBuilder(this).createScriptingService();
	}

	public FilesService getFilesService() {
		return this.filesService;
	}

	public DatabaseService getDatabaseService() {
		return this.databaseService;
	}

	public ScriptingService getScriptingService() {
		return this.scriptingService;
	}

	public BackupService getBackupService() {
		return this.backupService;
	}

	public PubSubService getPubSubService() {
		throw new NotImplementedException();
	}
}
