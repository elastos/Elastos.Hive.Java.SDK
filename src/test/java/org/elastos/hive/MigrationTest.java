package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.backup.State;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.CreateTargetVaultException;
import org.elastos.hive.exception.HiveException;
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
		CompletableFuture<Boolean> createFuture = testData.createTargetVault()
				.handleAsync((vault, throwable) -> {
					if (null != throwable) {
						throwable.printStackTrace();
					}
					return (null != vault && throwable == null);
				});

		CompletableFuture<Boolean> migrationFuture = managementApi.freezeVault()
				.thenComposeAsync(aBoolean ->
						backupApi.save(testData.getBackupAuthenticationHandler()))
				.thenApplyAsync(aBoolean -> {
					for (; ; ) {
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
						targetBackupApi.active()
				).handleAsync((aBoolean, throwable) -> {
					if (null != throwable) {
						throwable.printStackTrace();
					}
					return (aBoolean && (null == throwable));
				}).thenComposeAsync(aBoolean -> managementApi.unfreezeVault());

		CompletableFuture<Boolean> completableFuture = createFuture.thenComposeAsync(aBoolean -> {
			if (aBoolean) {
				return migrationFuture;
			}
			throw new CreateTargetVaultException();
		});

		try {
			assertTrue(completableFuture.get());
			assertTrue(completableFuture.isCompletedExceptionally() == false);
			assertTrue(completableFuture.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	private static TestData testData;

	private static Backup backupApi;
	private static Backup targetBackupApi;
	private static Management managementApi;

	@BeforeClass
	public static void setUp() {
		try {
			testData = TestData.getInstance();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
		managementApi = testData.getManagement().join();
		backupApi = testData.getBackup().join();
		targetBackupApi = testData.getTargetBackup().join();
	}
}
