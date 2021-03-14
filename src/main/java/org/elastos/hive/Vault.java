package org.elastos.hive;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.Database;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubSubService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.ServiceBuilder;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private Database	  	database;
	private ScriptingService scripting;
	private PubSubService pubsubService;
	private BackupService 	backupService;

	public Vault(AppContext context, String myDid) throws HiveException {
		super(context, null, myDid);
	}

	public Vault(AppContext context, String myDid, String providerAddress) throws HiveException {
		super(context, providerAddress, myDid);

		this.filesService 	= new ServiceBuilder(this).createFilesService();
		this.database 		= new ServiceBuilder(this).createDatabase();
		this.pubsubService 	= new ServiceBuilder(this).createPubsubService();
		this.backupService 	= new ServiceBuilder(this).createBackupService();
	}

	public FilesService getFilesService() {
		return this.filesService;
	}

	public Database getDatabase() {
		return this.database;
	}

	public ScriptingService getScripting() {
		return this.scripting;
	}

	public PubSubService getPubSubService() {
		return this.pubsubService;
	}

	public BackupService getBackupService() {
		return this.backupService;
	}
}
