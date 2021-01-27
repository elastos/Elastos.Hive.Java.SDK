package org.elastos.hive;

import org.elastos.hive.backup.State;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class MigrationTest {
	@Test
	public void testMigration() {
		CompletableFuture<Boolean> future = managementApi.freezeVault()
				.thenComposeAsync(aBoolean -> {
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
					for (;;) {
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						State state = backupApi.getState().join();
						if (state == State.STOP) {
							return true;
						}
					}
				}).thenComposeAsync(aBoolean ->
						client.getBackup(factory.getTargetDid(), factory.getTargetHost())
								.thenComposeAsync(backup -> backup.active())
				).handleAsync((aBoolean, throwable) -> {
					if (null != throwable) {
						throwable.printStackTrace();
					}
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


	private static AppInstanceFactory factory;
	static Backup backupApi;
	private static Management managementApi;
	private static Client client;

	@BeforeClass
	public static void setUp() {
		factory = AppInstanceFactory.configSelector();
		managementApi = factory.getManagement();
		backupApi = factory.getBackup();
		client = factory.getClient();
	}
}
