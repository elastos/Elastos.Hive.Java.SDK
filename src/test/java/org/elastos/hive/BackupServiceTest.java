package org.elastos.hive;

import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackupServiceTest {
	private static BackupService backupService;

	@BeforeAll
	public static void setUp() {
		try {
			backupService = TestData.getInstance().getBackupService();
		} catch (HiveException | DIDException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(1)
	void testCheckResult() {
		try {
			BackupService.BackupResult result = backupService.checkResult().get();
			Assertions.assertNotNull(result);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(2)
	void testStartBackup() {
		try {
			backupService.startBackup().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	@Order(3)
	void testRestoreFrom() {
		try {
			backupService.restoreFrom().get();
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

}
