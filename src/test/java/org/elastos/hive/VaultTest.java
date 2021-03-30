package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class VaultTest {
	private static Vault vault;

	@BeforeAll
	public static void setUp() {
		try {
			vault = TestData.getInstance().newVault();
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetFiles() {
	}

	@Test
	public void testGetMongoDb() {
	}

	@Test
	public void testGetProviderAddress() {
	}

	@Test
	public void testGetOwnerDid() {
	}

	@Test
	public void testGetVersion() {
		try {
			String version = vault.getVersion().exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(version);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetCommitHash() {
		try {
			String hash = vault.getCommitHash().exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(hash);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
