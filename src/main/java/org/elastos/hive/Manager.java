package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

public class Manager {
	private String providerAddress;
	private String ownerDid;
	private AuthHelper authHelper;

	private ServiceManager serviceManager;

	Manager(AuthHelper authHelper, String providerAddress, String ownerDid) {
		this.authHelper = authHelper;
		this.providerAddress = providerAddress;
		this.ownerDid = ownerDid;

		this.serviceManager = new ServiceManagerImpl(authHelper);
	}

	public CompletableFuture<Vault> createVault() {
		return this.serviceManager.createVault()
				.thenApplyAsync(aBoolean -> aBoolean?new Vault(this.authHelper, this.providerAddress, this.ownerDid):null);
	}

	public CompletableFuture<Boolean> checkVaultExist() {
		return this.serviceManager.getVaultServiceInfo()
				.handleAsync((usingPlan, throwable) ->
						(null==throwable && usingPlan!=null));
	}

	public CompletableFuture<Boolean> createBackup() {
		return this.serviceManager.createBackupVault()
				.handleAsync((aBoolean, throwable) -> (aBoolean && throwable==null));
	}

	public CompletableFuture<Boolean> checkBackupVaultExist() {
		return this.serviceManager.getBackupServiceInfo()
				.handleAsync((usingPlan, throwable) -> (null==throwable && usingPlan!=null));
	}


}
