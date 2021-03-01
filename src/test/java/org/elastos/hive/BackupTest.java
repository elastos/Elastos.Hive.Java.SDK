package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BackupTest {

	static Backup backupApi;

	@Test
	public void testGetState() {
		CompletableFuture<Boolean> future = backupApi.getState()
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSave() {
		CompletableFuture<Boolean> future = backupApi.save(testData.getBackupAuthenticationHandler())
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRestore() {
		CompletableFuture<Boolean> future = backupApi.restore(testData.getBackupAuthenticationHandler())
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static TestData testData;
	@BeforeClass
	public static void setUp() {
		try {
			testData = TestData.getInstance();
			backupApi = testData.getBackup().join();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

}
