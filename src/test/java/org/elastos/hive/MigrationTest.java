package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.backup.State;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MigrationTest {
	@Test
	public void testMigration() {
		CompletableFuture<Boolean> migrationFuture = managementApi.freezeVault()
				.thenComposeAsync(aBoolean ->
						backupApi.store(testData.getBackupAuthenticationHandler()))
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
						targetBackupApi.activate()
				);

		try {
			assertTrue(migrationFuture.get());
			assertTrue(migrationFuture.isCompletedExceptionally() == false);
			assertTrue(migrationFuture.isDone());
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

	@AfterClass
	public static void tearDown() {
		CompletableFuture<Boolean> future = managementApi.unfreezeVault();
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
