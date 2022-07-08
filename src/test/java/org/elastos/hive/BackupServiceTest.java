package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.AlreadyExistsException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.vault.backup.BackupResult;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupServiceTest {
	private static final Logger log = LoggerFactory.getLogger(BackupServiceTest.class);

	private static VaultSubscription subscription;
	private static BackupService backupService;
	private static BackupSubscription backupSubscription;

	@BeforeAll public static void setUp() throws ExecutionException, InterruptedException {
		trySubscribeVault();
		Assertions.assertDoesNotThrow(()->backupService = TestData.getInstance().getBackupService());
		trySubscribeBackup();
	}

	private static void trySubscribeVault() throws InterruptedException, ExecutionException {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
		try {
			subscription.subscribe().get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof AlreadyExistsException) {}
			else throw e;
		}
	}

	private static void trySubscribeBackup() throws ExecutionException, InterruptedException {
		Assertions.assertDoesNotThrow(()->backupSubscription = TestData.getInstance().newBackupSubscription());
		try {
			backupSubscription.subscribe().get();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof AlreadyExistsException) {}
			else throw e;
		}
	}

	@Disabled
	@Test @Order(1) void testStartBackup() {
		Assertions.assertDoesNotThrow(()->backupService.startBackup(
				(action, result, message) -> {
					log.debug("Backup progress: " + action + ";" + result + ";" + message);
					Assertions.assertTrue(result == BackupResult.Result.RESULT_PROCESS
						|| result == BackupResult.Result.RESULT_SUCCESS);
				}).get());
	}

	@Test @Order(2) void testCheckResult() {
		Assertions.assertDoesNotThrow(()-> {
			BackupResult result = backupService.checkResult().get();
			Assertions.assertNotNull(result);
			Assertions.assertNotNull(result.getState());
			Assertions.assertNotNull(result.getResult());
		});
	}

	@Disabled
	@Test @Order(3) void testStopBackup() {
		//TODO:
	}

	@Disabled
	@Test @Order(4) void testRestoreFrom() {
		Assertions.assertDoesNotThrow(()->backupService.restoreFrom(
				(action, result, message) -> {
					log.debug("Restore progress: " + action + ";" + result + ";" + message);
					Assertions.assertTrue(result == BackupResult.Result.RESULT_PROCESS
							|| result == BackupResult.Result.RESULT_SUCCESS);
				}).get());
	}

	@Disabled
	@Test @Order(5) void testStopRestore() {
		//TODO:
	}
}
