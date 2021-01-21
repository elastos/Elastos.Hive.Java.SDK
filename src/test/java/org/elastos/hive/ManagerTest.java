package org.elastos.hive;

import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class ManagerTest {

	@Test
	public void testCreateVault() {
		CompletableFuture<Boolean> future = managerApi.createVault()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testCreateBackup() {
		CompletableFuture<Boolean> future = managerApi.createBackup()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testDestroyVault() {
		CompletableFuture<Boolean> future = managerApi.destroyVault()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testFreezeVault() {
		CompletableFuture<Boolean> future = managerApi.freezeVault()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testUnfreezeVault() {
		CompletableFuture<Boolean> future = managerApi.unfreezeVault()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testGetVaultServiceInfo() {
		CompletableFuture<Boolean> future = managerApi.getVaultServiceInfo()
				.handle((vault, throwable) -> (null == throwable));

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
	public void testGetBackupServiceInfo() {
		CompletableFuture<Boolean> future = managerApi.getBackupServiceInfo()
				.handle((vault, throwable) -> (null == throwable));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static Manager managerApi;

	@BeforeClass
	public static void setUp() {
		managerApi = AppInstanceFactory.configSelector().getManager();
	}

}
