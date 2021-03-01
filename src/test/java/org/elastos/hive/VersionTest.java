package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class VersionTest {

	@Test
	public void getNodeVersion() throws ExecutionException, InterruptedException {
		vault.getNodeVersion().whenComplete((s, throwable) -> {
			assertNull(throwable);
			assertNotNull(s);
			System.out.println("nodeVersion:"+s);
		}).get();
	}

	@Test
	public void getNodeLastCommitId() throws ExecutionException, InterruptedException {
		vault.getNodeLastCommitId().whenComplete((s, throwable) -> {
			assertNull(throwable);
			assertNotNull(s);
			System.out.println("nodeVersion:"+s);
		}).get();
	}

	@Test
	public void getSdkVersion(){
		String version = SdkVersion.getVersion();
		assertNotNull(version);
	}

	private static Vault vault;
	@BeforeClass
	public static void setUp() {
		try {
			vault = TestData.getInstance().getVault().join();
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}
}
