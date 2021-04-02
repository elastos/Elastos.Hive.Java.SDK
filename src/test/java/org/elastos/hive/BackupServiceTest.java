package org.elastos.hive;

import org.elastos.hive.config.TestData;
import org.elastos.hive.service.BackupService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupServiceTest {
	private static BackupService backupService;

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()->backupService = TestData.getInstance().getBackupService());
	}

	@Test @Order(1) void testCheckResult() {
		Assertions.assertDoesNotThrow(()->{
			BackupService.BackupResult result = backupService.checkResult().get();
			Assertions.assertNotNull(result);
		});
	}

	@Test @Order(2) void testStartBackup() {
		Assertions.assertDoesNotThrow(()->backupService.startBackup().get());
	}

	@Test @Order(3) void testStopBackup() {
		//TODO:
	}

	@Test @Order(4) void testRestoreFrom() {
		Assertions.assertDoesNotThrow(()->backupService.restoreFrom().get());
	}

	@Test @Order(5) void testStopRestore() {
		//TODO:
	}
}
