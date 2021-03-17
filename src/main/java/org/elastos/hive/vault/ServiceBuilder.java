package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubSubService;
import org.elastos.hive.service.ScriptingService;

public class ServiceBuilder {
	private Vault vault;

	public ServiceBuilder(Vault vault) {
		this.vault = vault;
	}

	public FilesService createFilesService() {
		return new FilesServiceRender(vault);
	}

	public DatabaseService createDatabase() {
		return new DatabaseServiceRender(vault);
	}

	public ScriptingService createScriptingService() {
		return new ScriptingRender(vault);
	}

	public PubSubService createPubsubService() {
		return new PubSubServiceRender(vault);
	}

	public BackupService createBackupService() {
		return new BackupServiceRender(vault);
	}
}
