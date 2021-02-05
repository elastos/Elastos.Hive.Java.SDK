package org.elastos.hive.tests;

import org.elastos.hive.SdkVersion;
import org.elastos.hive.Vault;
import org.elastos.hive.didhelper.AppInstanceFactory;
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
		vault = AppInstanceFactory.configSelector().getVault();
	}
}
