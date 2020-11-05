package org.elastos.hive;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class VersionTest {

	@Test
	public void getNodeVersion() {
		try {
			vault.getNodeVersion().whenComplete((s, throwable) -> {
				assertNull(throwable);
				assertNotNull(s);
				System.out.println("nodeVersion:"+s);
			});
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void getNodeLastCommitId() {
		vault.getNodeLastCommitId().whenComplete((s, throwable) -> {
			assertNull(throwable);
			assertNotNull(s);
			System.out.println("nodeVersion:"+s);
		});
	}

	private static Vault vault;

	@BeforeClass
	public static void setUp() {
		vault = UserFactory.createUser1().getVault();
	}
}
