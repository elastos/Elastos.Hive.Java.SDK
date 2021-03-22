package org.elastos.hive;

import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubSubService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.ServiceBuilder;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private PubSubService pubsubService;
	private BackupService 	backupService;

	public Vault(AppContext context, String myDid) {
		super(context, null, myDid);
	}

	public Vault(AppContext context, String myDid, String providerAddress) {
		super(context, providerAddress, myDid);

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
}
