package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.Database;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubsubService;
import org.elastos.hive.service.ScriptingService;

public class ServiceBuilder {
	private Vault vault;

	public ServiceBuilder(Vault vault) {
		this.vault = vault;
	}

	public FilesService createFilesService() {
		return new FilesServiceRender(vault);
	}

	public Database createDatabase() {
		return new DatabaseRender(vault);
	}

	public ScriptingService createScriptingService() {
		return new ScriptingRender(vault);
	}

	public PubsubService createPubsubService() {
		return new PubsubServiceRender(vault);
	}

	public BackupService createBackupService() {
		return new BackupServiceRender(vault);
	}
}
