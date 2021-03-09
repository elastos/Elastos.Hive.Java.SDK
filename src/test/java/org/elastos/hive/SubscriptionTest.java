package org.elastos.hive;

import java.util.concurrent.CompletableFuture;

import org.elastos.did.DIDDocument;
import org.elastos.hive.exception.HiveException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SubscriptionTest {
	private AppContext appContext;

	@Test
	public void testSubscription() {
		try {
			VaultSubscription vs;

			// subscribe vault service;
			vs = new VaultSubscription(appContext, "your-vault-provider-address", "your-user-did");
			vs.subscribe("your-pricing-plan")
				.thenComposeAsync(info -> vs.activate())
			    .thenComposeAsync(aVoid -> vs.setBackup("your-backup-provider-address"));


			// subscribe backup service;
			BackupSubscription bs;
			bs = new BackupSubscription(appContext, "your-backup-provdier-address", "your-user-did");
			bs.subscribe("your-pricing-plan")
				.thenComposeAsync(info -> bs.activate());

		} catch (HiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@BeforeClass
	public void setup() {
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

	}

	@AfterClass
	public void teardown() {

	}
}
