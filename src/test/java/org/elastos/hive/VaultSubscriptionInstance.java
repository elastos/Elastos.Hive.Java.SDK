package org.elastos.hive;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CompletableFuture;

import org.elastos.did.DIDDocument;
import org.junit.Test;

public class VaultSubscriptionInstance {
	@Test
	public void testCreateVaultSubscription() {
		AppContext appContext;

		appContext = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public DIDDocument getAppInstanceDocument() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		assertNotNull(appContext);
	}
}
