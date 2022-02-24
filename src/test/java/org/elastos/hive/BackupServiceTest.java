package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.vault.backup.BackupResult;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupServiceTest {

	private static VaultSubscription subscription;
	private static BackupService backupService;
	private static BackupSubscription backupSubscription;

	@BeforeAll public static void setUp() {
		trySubscribeVault();
		Assertions.assertDoesNotThrow(()->backupService = TestData.getInstance().getBackupService());
		trySubscribeBackup();
	}

	private static void trySubscribeVault() {
		Assertions.assertDoesNotThrow(()->subscription = TestData.getInstance().newVaultSubscription());
		try {
			subscription.subscribe();
		} catch (NotFoundException e) {}
	}

	private static void trySubscribeBackup() {
		Assertions.assertDoesNotThrow(()->backupSubscription = TestData.getInstance().newBackupSubscription());
		try {
			backupSubscription.subscribe();
		} catch (NotFoundException e) {}
	}

	@Disabled
	@Test @Order(1) void testStartBackup() {
		Assertions.assertDoesNotThrow(()->backupService.startBackup().get());
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
		Assertions.assertDoesNotThrow(()->backupService.restoreFrom().get());
	}

	@Disabled
	@Test @Order(5) void testStopRestore() {
		//TODO:
	}
}
