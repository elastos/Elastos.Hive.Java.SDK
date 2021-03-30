package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.junit.jupiter.api.*;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BackupServiceTest {
	private static BackupService backupService;

	@BeforeAll
	public static void setUp() {
		try {
			backupService = TestData.getInstance().getBackupService();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(1)
	public void testCheckResult() {
		try {
			BackupService.BackupResult result = backupService.checkResult().exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(2)
	public void testStartBackup() {
		try {
			backupService.startBackup().exceptionally(e->{
				fail();
				return null;
			}).get();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(3)
	public void testRestoreFrom() {
		try {
			backupService.restoreFrom().exceptionally(e->{
				fail();
				return null;
			}).get();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
