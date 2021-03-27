package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BackupServiceTest {

	@Test
	public void test01CheckResult() {
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
	public void test02StartBackup() {
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
	public void test03RestoreFrom() {
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

	private static BackupService backupService;

	@BeforeClass
	public static void setUp() {
		try {
			backupService = TestData.getInstance().getBackupService();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

}
