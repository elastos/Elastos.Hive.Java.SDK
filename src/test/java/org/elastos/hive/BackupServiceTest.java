package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.service.BackupService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupServiceTest {

	private static VaultSubscription subscription;
	private static BackupService backupService;

	@BeforeAll public static void setUp() {
		trySubscribeVault();
		Assertions.assertDoesNotThrow(()->backupService = TestData.getInstance().getBackupService());
	}

	private static void trySubscribeVault() {
		Assertions.assertDoesNotThrow(()->{
			TestData testData = TestData.getInstance();
			subscription = new VaultSubscription(testData.getAppContext(), testData.getProviderAddress());
		});
		try {
			subscription.subscribe();
		} catch (NotFoundException e) {
			// do nothing.
		}
	}

	@Disabled
	@Test @Order(1) void testStartBackup() {
		Assertions.assertDoesNotThrow(()->backupService.startBackup().get());
	}

	@Disabled
	@Test @Order(2) void testCheckResult() {
		Assertions.assertDoesNotThrow(()->Assertions.assertNotNull(backupService.checkResult().get()));
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
