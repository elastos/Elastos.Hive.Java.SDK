package org.elastos.hive;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.didhelper.VaultAuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultHelperTest {


	private static Database database;

	@Test
	public void createCollect() {
		CompletableFuture<Boolean> future = database.createCollection("testCollection", null)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void generateMnemonic() {
		try {
			String mn = VaultAuthHelper.generateMnemonic("english");
			System.out.println(mn);
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

	@BeforeEach
	public void setUp() {
		try {
			Client client = AppInstanceFactory.getClientWithAuth();
			// Use AppInstanceFactory to get configuration items.
			AppInstanceFactory.Options options = AppInstanceFactory.configSelector().getOptions();
			client.createVault(options.getOwnerDid(), options.getProvider())
					.whenComplete((vault, throwable) -> {
						if (throwable == null) {
							database = vault.getDatabase();
						} else {
							try {
								database = client.getVault(options.getOwnerDid(), options.getProvider()).join()
										.getDatabase();
							} catch (Exception e) {
								throw new CompletionException(e);
							}
						}
					}).join();
		} catch (Exception e) {
			System.out.println("Vault already exist");
		}
	}
}
