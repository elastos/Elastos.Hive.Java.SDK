package org.elastos.hive;

import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.service.*;
import org.elastos.hive.subscription.VaultInfo;
import org.elastos.hive.vault.ExceptionConvertor;
import org.elastos.hive.vault.ServiceBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * This class explicitly represents the vault service subscribed by "userDid".
 */
public class Vault extends ServiceEndpoint implements ExceptionConvertor {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private PubSubService pubsubService;
	private BackupService 	backupService;

	public Vault(AppContext context, String providerAddress) {
		super(context, providerAddress);

		this.filesService 	= new ServiceBuilder(this).createFilesService();
		this.databaseService = new ServiceBuilder(this).createDatabase();
		this.pubsubService 	= new ServiceBuilder(this).createPubsubService();
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

	public PubSubService getPubSubService() {
		return this.pubsubService;
	}

	public BackupService getBackupService() {
		return this.backupService;
	}

	public CompletableFuture<VaultInfo> getInfo() {
		throw new NotImplementedException();
	}
}
