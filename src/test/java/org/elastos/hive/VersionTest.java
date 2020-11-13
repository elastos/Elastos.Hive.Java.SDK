package org.elastos.hive;

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
	public void getNodeLastCommitId() {
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

	@Test
	public void getSdkLastCommitId() {
		String lastCommit = SdkVersion.getLastCommitId();
		assertNotNull(lastCommit);
	}

	private static Vault vault;
	@BeforeClass
	public static void setUp() {
		vault = UserFactory.createUser2().getVault();
	}
}
