package org.elastos.hive;

import org.elastos.hive.didhelper.AppInstanceFactory;
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
		BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
			@Override
			public CompletableFuture<String> authorization(String serviceDid, String endPoint) {
				return null;
			}
		};
		CompletableFuture<Boolean> future = backupApi.save(handler)
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
		BackupAuthenticationHandler handler = new BackupAuthenticationHandler() {
			@Override
			public CompletableFuture<String> authorization(String serviceDid, String endPoint) {
				return null;
			}
		};
		CompletableFuture<Boolean> future = backupApi.restore(handler)
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
	public void testActive() {
		CompletableFuture<Boolean> future = backupApi.active()
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

	@BeforeClass
	public static void setUp() {
		Vault vault = AppInstanceFactory.configSelector().getVault();
		backupApi = vault.getBackup();
	}

}
