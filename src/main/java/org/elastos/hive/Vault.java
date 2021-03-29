package org.elastos.hive;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.NodeCommitHashResponseBody;
import org.elastos.hive.network.response.NodeVersionResponseBody;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubSubService;
import org.elastos.hive.service.ScriptingService;
import org.elastos.hive.vault.ServiceBuilder;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class explicitly represents the vault service subscribed by "myDid".
 */
public class Vault extends ServiceEndpoint {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private PubSubService pubsubService;
	private BackupService 	backupService;
	private NodeManageService nodeManageService;

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
		this.nodeManageService = new NodeManageService(this);
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
		return CompletableFuture.supplyAsync(() -> nodeManageService.getVersion());
	}

	public CompletableFuture<String> getCommitHash() {
		return CompletableFuture.supplyAsync(() -> nodeManageService.getCommitHash());
	}

	private class NodeManageService {
		private ConnectionManager connectionManager;

		NodeManageService(Vault vault) {
			this.connectionManager = vault.getAppContext().getConnectionManager();
		}

		public String getVersion() {
			try {
				NodeVersionResponseBody respBody = connectionManager.getNodeManagerApi()
						.version()
						.execute()
						.body();
				return HiveResponseBody.validateBody(respBody).getVersion();
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		}

		public String getCommitHash() {
			try {
				NodeCommitHashResponseBody respBody = connectionManager.getNodeManagerApi()
						.commitHash()
						.execute()
						.body();
				return HiveResponseBody.validateBody(respBody).getCommitHash();
			} catch (HiveException | IOException e) {
				throw new CompletionException(new HiveException(e.getMessage()));
			}
		}
	}
}
