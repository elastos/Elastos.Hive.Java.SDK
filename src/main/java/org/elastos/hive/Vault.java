package org.elastos.hive;

import org.elastos.hive.service.*;
import org.elastos.hive.vault.HttpExceptionHandler;
import org.elastos.hive.vault.NodeManageServiceRender;
import org.elastos.hive.vault.ServiceBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint implements HttpExceptionHandler {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private PubSubService pubsubService;
	private BackupService 	backupService;
	private NodeManageServiceRender nodeManageService;

	public Vault(AppContext context, String userDid, String providerAddress) {
		super(context, userDid, providerAddress);

		this.filesService 	= new ServiceBuilder(this).createFilesService();
		this.databaseService = new ServiceBuilder(this).createDatabase();
		this.pubsubService 	= new ServiceBuilder(this).createPubsubService();
		this.backupService 	= new ServiceBuilder(this).createBackupService();
		this.scriptingService = new ServiceBuilder(this).createScriptingService();
		this.nodeManageService = new NodeManageServiceRender(this);
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

	public CompletableFuture<String> getVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return nodeManageService.getVersion();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}

	public CompletableFuture<String> getCommitHash() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return nodeManageService.getCommitHash();
			} catch (Exception e) {
				throw new CompletionException(convertException(e));
			}
		});
	}
}
