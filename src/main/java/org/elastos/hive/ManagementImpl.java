package org.elastos.hive;

import org.elastos.hive.payment.UsingPlan;
import org.elastos.hive.service.BackupUsingPlan;

import java.util.concurrent.CompletableFuture;

public class ManagementImpl implements Management{
	private String providerAddress;
	private String ownerDid;
	private String targetHost;
	private AuthHelper authHelper;

	private ServiceManager serviceManager;
	private Payment payment;
	private Version version;

	ManagementImpl(AuthHelper authHelper, String providerAddress, String ownerDid, String targetHost) {
		this.authHelper = authHelper;
		this.providerAddress = providerAddress;
		this.ownerDid = ownerDid;
		this.targetHost = targetHost;

		this.serviceManager = new ServiceManagerImpl(authHelper);
		this.payment = new PaymentImpl(authHelper);
		this.version = new VersionImpl(authHelper);
	}

	@Override
	public CompletableFuture<Vault> createVault() {
		return serviceManager.createVault().thenApplyAsync(aBoolean ->
				aBoolean ? new Vault(this.authHelper, this.providerAddress, this.ownerDid) : null);

	}

	@Override
	public CompletableFuture<Boolean> destroyVault() {
		return this.serviceManager.removeVault();
	}

	@Override
	public CompletableFuture<Boolean> freezeVault() {
		return this.serviceManager.freezeVault();
	}

	@Override
	public CompletableFuture<Boolean> unfreezeVault() {
		return this.serviceManager.unfreezeVault();
	}

	@Override
	public CompletableFuture<Backup> createBackup() {
		return serviceManager.createBackup().thenApplyAsync(aBoolean ->
				aBoolean ? new BackupImpl(this.authHelper, this.targetHost) : null);

	}

	@Override
	public CompletableFuture<UsingPlan> getVaultServiceInfo() {
		return this.serviceManager.getVaultServiceInfo();
	}

	@Override
	public CompletableFuture<BackupUsingPlan> getBackupServiceInfo() {
		return this.serviceManager.getBackupServiceInfo();
	}

	@Override
	public CompletableFuture<Boolean> checkVaultExist() {
		return this.serviceManager.getVaultServiceInfo()
				.handleAsync((usingPlan, throwable) ->
						(null == throwable && usingPlan != null));
	}

	@Override
	public CompletableFuture<Boolean> checkBackupExist() {
		return this.serviceManager.getBackupServiceInfo()
				.handleAsync((usingPlan, throwable) -> (null == throwable && usingPlan != null));
	}

	@Override
	public Payment getPayment() {
		return this.payment;
	}

	@Override
	public Version getVersion() {
		return this.version;
	}
}
