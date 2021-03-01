package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class ManagementTest {

	@Test
	public void testCreateVault() {
		CompletableFuture<Boolean> future = managementApi.createVault()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
		CompletableFuture<Boolean> future = managementApi.createBackup()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
		CompletableFuture<Boolean> future = managementApi.destroyVault()
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
		CompletableFuture<Boolean> future = managementApi.freezeVault()
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
		CompletableFuture<Boolean> future = managementApi.unfreezeVault()
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
		CompletableFuture<Boolean> future = managementApi.getVaultServiceInfo()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
		CompletableFuture<Boolean> future = managementApi.getBackupServiceInfo()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
	public void testGetNodeVersion() {
		CompletableFuture<Boolean> future = managementApi.getVersion().getVersionCode()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

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
	public void testGetLastCommitId() {
		CompletableFuture<Boolean> future = managementApi.getVersion().getLastCommitId()
				.handle((vault, throwable) -> {
					if(throwable != null) {
						throwable.printStackTrace();
					}
					return (null == throwable);
				});

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static Management managementApi;

	@BeforeClass
	public static void setUp() {
		try {
			managementApi = TestData.getInstance().getManagement().join();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

}
