package org.elastos.hive.controller;

import org.elastos.hive.BackupAuthenticationHandler;
import org.elastos.hive.activites.MigrationActivity;
import org.elastos.hive.backup.State;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MigrationController extends Controller {

	private MigrationActivity migrationActivity;
	private String targetDid;
	private String targetHost;

	private static MigrationController mInstance = null;

	public static MigrationController newInstance(MigrationActivity migrationActivity, String targetDid, String targetHost) {
		if(mInstance == null) {
			mInstance = new MigrationController(migrationActivity, targetDid, targetHost);
		}

		return mInstance;
	}

	private MigrationController(MigrationActivity migrationActivity, String targetDid, String targetHost) {
		this.migrationActivity = migrationActivity;
		this.targetDid = targetDid;
		this.targetHost = targetHost;
	}
	
	@Override
	void execute() {
		migration();
	}

	public void migration() {
		CompletableFuture<Boolean> future = migrationActivity.getManagement().freezeVault()
				.thenComposeAsync(aBoolean -> {
					BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
						@Override
						public CompletableFuture<String> getAuthorization(String serviceDid) {
							return CompletableFuture.supplyAsync(() ->
									migrationActivity.getBackupVc(serviceDid));
						}

						@Override
						public String getTargetHost() {
							return targetHost;
						}

						@Override
						public String getTargetDid() {
							return targetDid;
						}
					};
					return migrationActivity.getBackup().save(handler);
				}).thenApplyAsync(aBoolean -> {
					for (;;) {
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						State state = migrationActivity.getBackup().getState().join();
						if (state == State.STOP) {
							return true;
						}
					}
				}).thenComposeAsync(aBoolean ->
						migrationActivity.getClient().getBackup(targetDid, targetHost)
								.thenComposeAsync(backup -> backup.active())
				).handleAsync((aBoolean, throwable) -> {
					if (null != throwable) {
						throwable.printStackTrace();
					}
					migrationActivity.getManagement().unfreezeVault();
					return (aBoolean && (null == throwable));
				});


		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
