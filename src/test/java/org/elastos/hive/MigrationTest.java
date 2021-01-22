package org.elastos.hive;

import org.elastos.hive.backup.State;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

@Ignore
public class MigrationTest {
	@Test
	public void testMigration() {
		managerApi.freezeVault().thenComposeAsync(aBoolean -> {
			BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
				@Override
				public CompletableFuture<String> getAuthorization(String serviceDid) {
					return CompletableFuture.supplyAsync(() ->
							factory.getBackupVc(serviceDid));
				}

				@Override
				public String getTargetHost() {
					return factory.getTargetHost();
				}

				@Override
				public String getTargetDid() {
					return factory.getTargetDid();
				}
			};
			return backupApi.save(handler);
		}).thenApplyAsync(aBoolean -> {
			for(;;) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				State state = backupApi.getState().join();
				if(state == State.STOP) {
					return true;
				}
			}
		}).thenComposeAsync(aBoolean -> backupApi.active())
		.handleAsync((aBoolean, throwable) -> (aBoolean && (null==throwable)));
	}


	private static AppInstanceFactory factory;
	static Backup backupApi;
	private static Manager managerApi;
	@BeforeClass
	public static void setUp() {
		factory = AppInstanceFactory.configSelector();
		managerApi = factory.getManager();
		backupApi = factory.getBackup();
	}
}
