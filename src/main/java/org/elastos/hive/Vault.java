package org.elastos.hive;

import org.elastos.hive.exception.UnsupportedMethodException;
import org.elastos.hive.service.*;
import org.elastos.hive.vault.HttpExceptionHandler;
import org.elastos.hive.vault.NodeManageServiceRender;
import org.elastos.hive.vault.ServiceBuilder;

import java.util.Date;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This class explicitly represents the vault service subscribed by "userDid".
 */
public class Vault extends ServiceEndpoint implements HttpExceptionHandler {
	private FilesService 	filesService;
	private DatabaseService databaseService;
	private ScriptingService scriptingService;
	private PubSubService pubsubService;
	private BackupService 	backupService;
	private NodeManageServiceRender nodeManageService;

	public class PropertySet {
		private String serviceDid;
		private String pricingPlan;
		private long created;
		private long updated;
		private long quota;
		private long used;

		public String getServiceId() {
			return serviceDid;
		}

		public String getPricingPlan() {
			return pricingPlan;
		}

		public Date getCreated() {
			return new Date(created);
		}

		public Date getLastUpdated() {
			return new Date(updated);
		}

		public long getQuotaSpace() {
			return quota;
		}

		public long getUsedSpace() {
			return used;
		}

		PropertySet setServiceId(String serviceId) {
			this.serviceDid = serviceId;
			return this;
		}

		PropertySet setPricingPlan(String pricingPlan) {
			this.pricingPlan = pricingPlan;
			return this;
		}

		PropertySet setCreated(long created) {
			this.created = created;
			return this;
		}

		PropertySet setUpdated(long updated) {
			this.updated = updated;
			return this;
		}

		PropertySet setQuota(long quota) {
			this.quota = quota;
			return this;
		}

		PropertySet setUsedSpace(long used) {
			this.used = used;
			return this;
		}
	};

	public Vault(AppContext context, String providerAddress) {
		super(context, providerAddress);

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

	public CompletableFuture<PropertySet> getPropertySet() {
		throw new UnsupportedMethodException();
	}

	/*
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
	*/
}
