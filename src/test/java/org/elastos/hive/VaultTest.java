package org.elastos.hive;

import com.google.common.base.Throwables;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.exception.HiveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class VaultTest {
	private static Vault vault;

	@BeforeAll
	public static void setUp() {
		try {
			vault = TestData.getInstance().newVault();
		} catch (HiveException | DIDException e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	void testGetFiles() {
	}

	@Test
	void testGetMongoDb() {
	}

	@Test
	void testGetProviderAddress() {
	}

	@Test
	void testGetOwnerDid() {
	}

	@Test
	void testGetVersion() {
		try {
			String version = vault.getVersion().get();
			Assertions.assertNotNull(version);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}

	@Test
	void testGetCommitHash() {
		try {
			String hash = vault.getCommitHash().get();
			Assertions.assertNotNull(hash);
		} catch (Exception e) {
			Assertions.fail(Throwables.getStackTraceAsString(e));
		}
	}
}
