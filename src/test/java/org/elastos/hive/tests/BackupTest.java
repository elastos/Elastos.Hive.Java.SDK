package org.elastos.hive.tests;

import org.elastos.hive.Backup;
import org.elastos.hive.BackupAuthenticationHandler;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
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
			public CompletableFuture<String> getAuthorization(String serviceDid) {
				return CompletableFuture.supplyAsync(() ->
						factory.getBackupVc(serviceDid));
			}

			@Override
			public String getTargetHost() {
				return factory.getTargetHost();
			}

			@Override
			public String getTargetDid() {
				return factory.getTargetDid();
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
			public CompletableFuture<String> getAuthorization(String serviceDid) {
				return CompletableFuture.supplyAsync(() ->
						factory.getBackupVc(serviceDid));
			}

			@Override
			public String getTargetHost() {
				return factory.getTargetHost();
			}

			@Override
			public String getTargetDid() {
				return factory.getTargetDid();
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

//	@Test
//	public void testActive() {
//		CompletableFuture<Boolean> future = backupApi.active()
//				.handle((success, ex) -> (ex == null));
//
//		try {
//			assertTrue(future.get());
//			assertTrue(future.isCompletedExceptionally() == false);
//			assertTrue(future.isDone());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//	}

	private static AppInstanceFactory factory;
	@BeforeClass
	public static void setUp() {
		factory = AppInstanceFactory.configSelector();
		backupApi = factory.getBackup();
	}

}
